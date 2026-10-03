package com.bookingapp.api.listing;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalTime;

/** Times are in the hotel's local time, for example 14:00 and 12:00. */
public record HotelDetailsDto(@Min(1) @Max(5) Integer stars, LocalTime checkInTime, LocalTime checkOutTime) {
}
