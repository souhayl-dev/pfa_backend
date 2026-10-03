package com.bookingapp.application.booking;

import com.bookingapp.domain.booking.BookingStatus;
import com.bookingapp.domain.booking.BookingStatusChange;

import java.time.Instant;
import java.util.UUID;

/** fromStatus is null for the first entry; changedBy is null when the system made the change. */
public record StatusChangeView(BookingStatus fromStatus, BookingStatus toStatus, UUID changedBy, String reason,
                               Instant changedAt) {

    public static StatusChangeView from(BookingStatusChange change) {
        return new StatusChangeView(change.fromStatus(), change.toStatus(), change.changedBy(), change.reason(),
                change.changedAt());
    }
}
