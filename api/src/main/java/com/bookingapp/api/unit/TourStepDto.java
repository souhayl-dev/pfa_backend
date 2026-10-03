package com.bookingapp.api.unit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record TourStepDto(@Positive int stepOrder, @Positive int dayNumber, @NotBlank @Size(max = 100) String city,
                          @Size(max = 1000) String description) {
}
