package com.bookingapp.api.profile;

import com.bookingapp.domain.user.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @NotBlank @Size(max = 100) String firstName,
        @NotBlank @Size(max = 100) String lastName,
        @Size(max = 50) String username,
        @Size(max = 30) String phone,
        Gender gender,
        @NotBlank @Pattern(regexp = "[A-Z]{3}", message = "must be a 3-letter currency code") String preferredCurrency,
        boolean notificationsEnabled,
        @Size(max = 500) String profileImage) {
}
