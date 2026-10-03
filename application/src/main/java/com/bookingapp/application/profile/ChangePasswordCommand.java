package com.bookingapp.application.profile;

import java.util.UUID;

public record ChangePasswordCommand(UUID userId, String currentRawPassword, String newRawPassword) {
}
