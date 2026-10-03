package com.bookingapp.api.listing;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TravelAgencyDetailsDto(@NotBlank @Size(max = 100) String licenseNumber) {
}
