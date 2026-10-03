package com.bookingapp.domain.unit;

/** How availability is checked for a unit. It follows from the unit's type. */
public enum BookingMode {
    /** One booking at a time: a room or a car cannot have overlapping bookings. */
    EXCLUSIVE,
    /** Shared seats: bookings at the same moment are fine while their guests fit in the capacity. */
    SHARED_BY_OVERLAP,
    /** Shared seats per departure: bookings starting at the same moment form one group up to the capacity. */
    SHARED_BY_START
}
