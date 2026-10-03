package com.bookingapp.application.review;

import com.bookingapp.application.catalog.PublicListings;
import com.bookingapp.application.shared.ProviderAccess;
import com.bookingapp.application.shared.port.UnitOfWork;
import com.bookingapp.domain.booking.Booking;
import com.bookingapp.domain.booking.BookingRepository;
import com.bookingapp.domain.client.Client;
import com.bookingapp.domain.client.ClientRepository;
import com.bookingapp.domain.listing.Listing;
import com.bookingapp.domain.listing.ListingRepository;
import com.bookingapp.domain.review.Review;
import com.bookingapp.domain.review.ReviewRepository;
import com.bookingapp.domain.shared.exception.BusinessRuleException;
import com.bookingapp.domain.shared.exception.EntityNotFoundException;
import com.bookingapp.domain.shared.exception.UnauthorizedActionException;
import com.bookingapp.domain.unit.BookableUnit;
import com.bookingapp.domain.unit.BookableUnitRepository;
import com.bookingapp.domain.user.User;
import com.bookingapp.domain.user.UserRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ReviewService implements ReviewUseCase {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final BookableUnitRepository unitRepository;
    private final ListingRepository listingRepository;
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final PublicListings publicListings;
    private final ProviderAccess access;
    private final UnitOfWork unitOfWork;

    public ReviewService(ReviewRepository reviewRepository, BookingRepository bookingRepository,
                         BookableUnitRepository unitRepository, ListingRepository listingRepository,
                         ClientRepository clientRepository, UserRepository userRepository,
                         PublicListings publicListings, ProviderAccess access, UnitOfWork unitOfWork) {
        this.reviewRepository = reviewRepository;
        this.bookingRepository = bookingRepository;
        this.unitRepository = unitRepository;
        this.listingRepository = listingRepository;
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
        this.publicListings = publicListings;
        this.access = access;
        this.unitOfWork = unitOfWork;
    }

    @Override
    public List<ReviewView> ofListing(UUID listingId) {
        publicListings.require(listingId);
        return reviewRepository.findByListing(listingId).stream()
                .map(review -> view(review, clientOf(findBooking(review.bookingId()))))
                .toList();
    }

    /** The listing row is locked while its cached rating changes, so two reviews cannot overwrite each other. */
    @Override
    public ReviewView write(UUID userId, UUID bookingId, int rating, String comment) {
        return unitOfWork.inTransaction(() -> {
            Booking booking = findBooking(bookingId);
            Client client = requireClientOf(userId, booking);
            if (reviewRepository.findByBooking(bookingId).isPresent()) {
                throw new BusinessRuleException("this booking has already been reviewed");
            }
            Review review = Review.write(UUID.randomUUID(), booking, rating, comment, Instant.now());
            Listing listing = lockListingOf(booking);
            listing.applyReviewChange(null, review.rating());
            listingRepository.save(listing);
            return view(reviewRepository.save(review), client);
        });
    }

    @Override
    public ReviewView edit(UUID userId, UUID reviewId, int rating, String comment) {
        return unitOfWork.inTransaction(() -> {
            Review review = findReview(reviewId);
            Booking booking = findBooking(review.bookingId());
            Client client = requireClientOf(userId, booking);
            int previousRating = review.rating();
            review.edit(rating, comment, Instant.now());
            Listing listing = lockListingOf(booking);
            listing.applyReviewChange(previousRating, review.rating());
            listingRepository.save(listing);
            return view(reviewRepository.save(review), client);
        });
    }

    @Override
    public ReviewView reply(UUID userId, UUID reviewId, String text) {
        Review review = findReview(reviewId);
        Booking booking = findBooking(review.bookingId());
        access.listing(userId, listingIdOf(booking));
        review.reply(text, userId, Instant.now());
        return view(reviewRepository.save(review), clientOf(booking));
    }

    private Client requireClientOf(UUID userId, Booking booking) {
        return clientRepository.findByUserId(userId)
                .filter(client -> client.id().equals(booking.clientId()))
                .orElseThrow(() -> new UnauthorizedActionException("only the client who booked can review it"));
    }

    private Client clientOf(Booking booking) {
        return clientRepository.findById(booking.clientId()).orElse(null);
    }

    private ReviewView view(Review review, Client client) {
        String author = client == null ? null
                : userRepository.findById(client.userId()).map(User::firstName).orElse(null);
        return ReviewView.from(review, author);
    }

    private UUID listingIdOf(Booking booking) {
        return unitRepository.findById(booking.unitId())
                .map(BookableUnit::listingId)
                .orElseThrow(() -> new EntityNotFoundException("Unit", booking.unitId()));
    }

    private Listing lockListingOf(Booking booking) {
        UUID listingId = listingIdOf(booking);
        return listingRepository.findByIdForUpdate(listingId)
                .orElseThrow(() -> new EntityNotFoundException("Listing", listingId));
    }

    private Review findReview(UUID reviewId) {
        return reviewRepository.findById(reviewId).orElseThrow(() -> new EntityNotFoundException("Review", reviewId));
    }

    private Booking findBooking(UUID bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking", bookingId));
    }
}
