package com.bookingapp.application.profile;

import java.time.LocalDate;
import java.util.UUID;

/** nationality is a 2-letter ISO country code. Both fields are optional. */
public record UpdateClientDetailsCommand(UUID userId, String nationality, LocalDate birthDate) {
}
