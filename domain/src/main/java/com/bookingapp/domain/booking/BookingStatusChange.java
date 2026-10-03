package com.bookingapp.domain.booking;

import com.bookingapp.domain.shared.Require;

import java.time.Instant;
import java.util.UUID;

/**
 * One row of a booking's history. fromStatus is null for the first entry, and changedBy is null
 * when the system made the change (for example automatic completion).
 */
public record BookingStatusChange(UUID id, UUID bookingId, BookingStatus fromStatus, BookingStatus toStatus,
                                  UUID changedBy, String reason, Instant changedAt) {

    public BookingStatusChange {
        Require.notNull(id, "id");
        Require.notNull(bookingId, "bookingId");
        Require.notNull(toStatus, "toStatus");
        reason = Require.optional(reason, "reason", 500);
        Require.notNull(changedAt, "changedAt");
    }
}
