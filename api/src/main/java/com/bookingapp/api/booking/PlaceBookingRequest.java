package com.bookingapp.api.booking;

import com.bookingapp.application.booking.PlaceBookingCommand;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * start and end are in the listing's local time, without a timezone, for example 2026-11-10T14:00.
 * Rooms use only their dates, a tour only needs start, and tables and transfers may leave end out.
 */
public record PlaceBookingRequest(
        @NotNull UUID unitId,
        LocalDateTime start,
        LocalDateTime end,
        @Positive int guestsCount,
        @Size(max = 1000) String specialRequests) {

    public PlaceBookingCommand toCommand() {
        return new PlaceBookingCommand(unitId, start, end, guestsCount, specialRequests);
    }
}
