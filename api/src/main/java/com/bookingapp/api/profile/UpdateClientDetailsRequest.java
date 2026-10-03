package com.bookingapp.api.profile;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record UpdateClientDetailsRequest(
        @Pattern(regexp = "[A-Z]{2}", message = "must be a 2-letter country code") String nationality,
        @Past LocalDate birthDate) {
}
