package com.bookingapp.api.unit;

import com.bookingapp.domain.unit.UnitDetails;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/** The number of seats is the unit's capacity. */
public record CarDetailsDto(
        @NotBlank @Size(max = 50) String brand,
        @NotBlank @Size(max = 50) String model,
        @Min(1950) @Max(2100) int year,
        @NotNull UnitDetails.CarCategory category,
        @NotNull UnitDetails.Transmission transmission,
        @NotNull UnitDetails.FuelType fuelType,
        @Min(1) @Max(10) int doors,
        boolean hasAc,
        @NotBlank @Size(max = 20) String plateNumber,
        @Positive Integer mileageLimitKm) {
}
