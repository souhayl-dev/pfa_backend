package com.bookingapp.api.unit;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

/** Steps are numbered 1, 2, 3... in day order, and each day falls within durationDays. */
public record TourDetailsDto(@Positive int durationDays, @NotNull @Valid List<TourStepDto> steps) {
}
