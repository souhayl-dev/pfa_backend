package com.bookingapp.application.auth;

public record LoginCommand(String email, String rawPassword) {
}
