package com.bookingapp.domain.booking;

import java.util.EnumSet;
import java.util.Set;

public enum BookingStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
    COMPLETED,
    NO_SHOW;

    /**
     * PENDING   -> CONFIRMED, CANCELLED
     * CONFIRMED -> CANCELLED, COMPLETED, NO_SHOW
     * CANCELLED, COMPLETED and NO_SHOW are final.
     */
    public Set<BookingStatus> allowedNext() {
        return switch (this) {
            case PENDING -> EnumSet.of(CONFIRMED, CANCELLED);
            case CONFIRMED -> EnumSet.of(CANCELLED, COMPLETED, NO_SHOW);
            case CANCELLED, COMPLETED, NO_SHOW -> EnumSet.noneOf(BookingStatus.class);
        };
    }

    public boolean canMoveTo(BookingStatus next) {
        return allowedNext().contains(next);
    }

    /** Pending and confirmed bookings hold their units; the other statuses free them. */
    public boolean holdsAvailability() {
        return this == PENDING || this == CONFIRMED;
    }
}
