package com.bookingapp.application.profile;

import com.bookingapp.domain.user.Gender;

import java.util.UUID;

/** The email cannot change here: it identifies the account and would need a new verification. */
public record UpdateProfileCommand(UUID userId, String firstName, String lastName, String username, String phone,
                                   Gender gender, boolean notificationsEnabled,
                                   String profileImage) {
}
