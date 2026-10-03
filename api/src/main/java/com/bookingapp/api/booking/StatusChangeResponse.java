package com.bookingapp.api.booking;

import com.bookingapp.application.booking.StatusChangeView;
import com.bookingapp.domain.booking.BookingStatus;

import java.time.Instant;
import java.util.UUID;

/** fromStatus is null for the first entry; changedBy is null when the system made the change. */
public record StatusChangeResponse(BookingStatus fromStatus, BookingStatus toStatus, UUID changedBy, String reason,
                                   Instant changedAt) {

    public static StatusChangeResponse from(StatusChangeView view) {
        return new StatusChangeResponse(view.fromStatus(), view.toStatus(), view.changedBy(), view.reason(),
                view.changedAt());
    }
}
