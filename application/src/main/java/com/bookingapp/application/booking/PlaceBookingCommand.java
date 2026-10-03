package com.bookingapp.application.booking;

import java.time.LocalDateTime;
import java.util.UUID;

/** start and end are in the listing's local time; see BookingPeriods for how each unit type uses them. */
public record PlaceBookingCommand(UUID unitId, LocalDateTime start, LocalDateTime end, int guestsCount,
                                  String specialRequests) {
}
