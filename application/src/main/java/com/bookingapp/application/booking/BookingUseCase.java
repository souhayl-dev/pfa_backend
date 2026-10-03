package com.bookingapp.application.booking;

import com.bookingapp.domain.booking.BookingStatus;

import java.util.List;
import java.util.UUID;

public interface BookingUseCase {
    /** Books one unit for one period. The booking starts PENDING until the provider confirms it. */
    BookingView place(UUID userId, PlaceBookingCommand command);

    List<BookingView> myBookings(UUID userId);

    /** Visible to the client who booked and to the team of the listing's provider. */
    BookingView get(UUID userId, UUID bookingId);

    List<StatusChangeView> history(UUID userId, UUID bookingId);

    /** Clients can cancel until the booking starts; the provider's team can cancel while it is pending or confirmed. */
    BookingView cancel(UUID userId, UUID bookingId, String reason);

    /** For the provider's team. status may be null for every status. */
    List<BookingView> listingBookings(UUID userId, UUID listingId, BookingStatus status);

    BookingView confirm(UUID userId, UUID bookingId);

    BookingView markNoShow(UUID userId, UUID bookingId);

    /** Run by the scheduler: completes confirmed bookings that have ended. Returns how many. */
    int completeEnded();
}
