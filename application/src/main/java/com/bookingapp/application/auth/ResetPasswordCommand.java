package com.bookingapp.application.auth;

public record ResetPasswordCommand(String token, String newRawPassword) {
}
