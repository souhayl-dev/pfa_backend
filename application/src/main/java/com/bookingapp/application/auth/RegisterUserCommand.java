package com.bookingapp.application.auth;

public record RegisterUserCommand(String firstName, String lastName, String email, String rawPassword) {
}
