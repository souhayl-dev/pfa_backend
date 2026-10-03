package com.bookingapp.domain.booking;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookingRepository {
    /** Saves the booking and the status changes it recorded since it was loaded. */
    Booking save(Booking booking);

    Optional<Booking> findById(UUID id);

    boolean existsByCode(String code);

    /** Newest first. */
    List<Booking> findByClient(UUID clientId);

    /** Bookings of every unit of a listing, newest first. status may be null for every status. */
    List<Booking> findByListing(UUID listingId, BookingStatus status);

    /** Bookings of this unit overlapping [startAt, endAt) that still hold availability (PENDING or CONFIRMED). */
    List<Booking> findHeld(UUID unitId, Instant startAt, Instant endAt);

    /** CONFIRMED bookings that ended before the given instant: candidates for completion. */
    List<Booking> findConfirmedEndedBefore(Instant instant);

    /** Oldest first. */
    List<BookingStatusChange> findHistory(UUID bookingId);
}
