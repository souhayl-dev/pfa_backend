package com.bookingapp.api.listing;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CarRentalAgencyDetailsDto(
        @NotBlank @Size(max = 100) String licenseNumber,
        @Min(18) @Max(99) int minDriverAge,
        @NotNull @PositiveOrZero BigDecimal depositAmount) {
}
