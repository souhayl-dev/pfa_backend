package com.bookingapp.domain.review;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository {
    Review save(Review review);

    Optional<Review> findById(UUID id);

    Optional<Review> findByBooking(UUID bookingId);

    /** Reviews of a listing, newest first, found through their bookings. */
    List<Review> findByListing(UUID listingId);
}
