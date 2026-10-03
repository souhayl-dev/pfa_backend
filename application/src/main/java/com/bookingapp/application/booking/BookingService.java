package com.bookingapp.application.booking;

import com.bookingapp.application.booking.BookingPeriods.Period;
import com.bookingapp.application.catalog.PublicListings;
import com.bookingapp.application.shared.ProviderAccess;
import com.bookingapp.application.shared.port.UnitOfWork;
import com.bookingapp.domain.booking.AvailabilityPolicy;
import com.bookingapp.domain.booking.Booking;
import com.bookingapp.domain.booking.BookingCode;
import com.bookingapp.domain.booking.BookingRepository;
import com.bookingapp.domain.booking.BookingStatus;
import com.bookingapp.domain.booking.PricingPolicy;
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

import java.security.SecureRandom;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class BookingService implements BookingUseCase {

    private final BookingRepository bookingRepository;
    private final BookableUnitRepository unitRepository;
    private final ListingRepository listingRepository;
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final PublicListings publicListings;
    private final ProviderAccess access;
    private final UnitOfWork unitOfWork;
    private final SecureRandom random = new SecureRandom();

    public BookingService(BookingRepository bookingRepository, BookableUnitRepository unitRepository,
                          ListingRepository listingRepository, ClientRepository clientRepository,
                          UserRepository userRepository, ReviewRepository reviewRepository,
                          PublicListings publicListings, ProviderAccess access, UnitOfWork unitOfWork) {
        this.bookingRepository = bookingRepository;
        this.unitRepository = unitRepository;
        this.listingRepository = listingRepository;
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
        this.publicListings = publicListings;
        this.access = access;
        this.unitOfWork = unitOfWork;
    }

    /**
     * The unit row is locked (SELECT ... FOR UPDATE) before its availability is read, and the lock is
     * held until the booking is saved, so two clients can never book the same room for the same night.
     * The client profile is created here if this is the user's first booking.
     */
    @Override
    public BookingView place(UUID userId, PlaceBookingCommand command) {
        return unitOfWork.inTransaction(() -> {
            BookableUnit unit = unitRepository.findByIdForUpdate(command.unitId())
                    .orElseThrow(() -> new EntityNotFoundException("Unit", command.unitId()));
            Listing listing = publicListings.require(unit.listingId());
            User user = findUser(userId);
            user.requireActive();
            if (access.isMemberOf(userId, listing.providerId())) {
                throw new BusinessRuleException("you cannot book a listing of a provider you work for");
            }

            Instant now = Instant.now();
            Period period = BookingPeriods.resolve(listing, unit, command.start(), command.end());
            AvailabilityPolicy.checkInFuture(unit, period.startAt(), now);
            AvailabilityPolicy.checkAvailable(unit, period.startAt(), period.endAt(), command.guestsCount(),
                    bookingRepository.findHeld(unit.id(), period.startAt(), period.endAt()));
            var quote = PricingPolicy.quote(unit, period.startAt(), period.endAt(), command.guestsCount(),
                    listing.location().timezone());

            Client client = clientRepository.findByUserId(userId)
                    .orElseGet(() -> clientRepository.save(Client.create(UUID.randomUUID(), userId, now)));
            Booking booking = Booking.place(UUID.randomUUID(), newCode(), client.id(), listing, unit,
                    period.startAt(), period.endAt(), command.guestsCount(), quote.unitPrice(), quote.total(),
                    command.specialRequests(), userId, UUID.randomUUID(), now);
            return view(bookingRepository.save(booking), false);
        });
    }

    private String newCode() {
        String code;
        do {
            code = BookingCode.generate(random);
        } while (bookingRepository.existsByCode(code));
        return code;
    }

    @Override
    public List<BookingView> myBookings(UUID userId) {
        return clientRepository.findByUserId(userId)
                .map(client -> bookingRepository.findByClient(client.id()).stream()
                        .map(booking -> view(booking, false))
                        .toList())
                .orElse(List.of());
    }

    @Override
    public BookingView get(UUID userId, UUID bookingId) {
        Booking booking = findBooking(bookingId);
        if (isClientOf(userId, booking)) {
            return view(booking, false);
        }
        requireTeamOf(userId, booking);
        return view(booking, true);
    }

    @Override
    public List<StatusChangeView> history(UUID userId, UUID bookingId) {
        get(userId, bookingId);
        return bookingRepository.findHistory(bookingId).stream().map(StatusChangeView::from).toList();
    }

    @Override
    public BookingView cancel(UUID userId, UUID bookingId, String reason) {
        return unitOfWork.inTransaction(() -> {
            Booking booking = findBooking(bookingId);
            Instant now = Instant.now();
            boolean asTeam = !isClientOf(userId, booking);
            if (asTeam) {
                requireTeamOf(userId, booking);
            } else if (booking.hasStarted(now)) {
                throw new BusinessRuleException("a booking can no longer be cancelled once it has started");
            }
            booking.cancel(UUID.randomUUID(), userId, reason, now);
            return view(bookingRepository.save(booking), asTeam);
        });
    }

    @Override
    public List<BookingView> listingBookings(UUID userId, UUID listingId, BookingStatus status) {
        access.listing(userId, listingId);
        return bookingRepository.findByListing(listingId, status).stream()
                .map(booking -> view(booking, true))
                .toList();
    }

    @Override
    public BookingView confirm(UUID userId, UUID bookingId) {
        return unitOfWork.inTransaction(() -> {
            Booking booking = findBooking(bookingId);
            requireTeamOf(userId, booking);
            booking.confirm(UUID.randomUUID(), userId, Instant.now());
            return view(bookingRepository.save(booking), true);
        });
    }

    @Override
    public BookingView markNoShow(UUID userId, UUID bookingId) {
        return unitOfWork.inTransaction(() -> {
            Booking booking = findBooking(bookingId);
            requireTeamOf(userId, booking);
            booking.markNoShow(UUID.randomUUID(), userId, Instant.now());
            return view(bookingRepository.save(booking), true);
        });
    }

    @Override
    public int completeEnded() {
        Instant now = Instant.now();
        List<Booking> ended = bookingRepository.findConfirmedEndedBefore(now);
        for (Booking booking : ended) {
            unitOfWork.inTransaction(() -> {
                booking.complete(UUID.randomUUID(), now);
                bookingRepository.save(booking);
            });
        }
        return ended.size();
    }

    private boolean isClientOf(UUID userId, Booking booking) {
        return clientRepository.findByUserId(userId)
                .map(client -> client.id().equals(booking.clientId()))
                .orElse(false);
    }

    /** Someone who is neither the client nor on the provider's team learns nothing about the booking. */
    private void requireTeamOf(UUID userId, Booking booking) {
        try {
            access.listing(userId, findUnit(booking.unitId()).listingId());
        } catch (UnauthorizedActionException e) {
            throw new UnauthorizedActionException("this booking is not yours");
        }
    }

    private BookingView view(Booking booking, boolean forTeam) {
        BookableUnit unit = findUnit(booking.unitId());
        Listing listing = listingRepository.findById(unit.listingId())
                .orElseThrow(() -> new EntityNotFoundException("Listing", unit.listingId()));
        String clientName = forTeam
                ? clientRepository.findById(booking.clientId())
                        .flatMap(client -> userRepository.findById(client.userId()))
                        .map(User::fullName)
                        .orElse(null)
                : null;
        UUID reviewId = reviewRepository.findByBooking(booking.id()).map(Review::id).orElse(null);
        return new BookingView(booking.id(), booking.code(), listing.id(), listing.name(),
                listing.location().timezone().getId(), unit.id(), unit.name(), unit.type(), booking.status(),
                booking.startAt(), booking.endAt(), booking.guestsCount(), booking.unitPrice(),
                booking.total().amount(), booking.total().currency(), booking.specialRequests(), clientName,
                reviewId, booking.createdAt(), booking.updatedAt());
    }

    private Booking findBooking(UUID bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking", bookingId));
    }

    private BookableUnit findUnit(UUID unitId) {
        return unitRepository.findById(unitId).orElseThrow(() -> new EntityNotFoundException("Unit", unitId));
    }

    private User findUser(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User", userId));
    }
}
