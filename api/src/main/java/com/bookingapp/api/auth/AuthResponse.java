package com.bookingapp.api.auth;

import com.bookingapp.api.profile.UserResponse;
import com.bookingapp.application.auth.AuthResult;

public record AuthResponse(String token, UserResponse user) {
    public static AuthResponse from(AuthResult result) {
        return new AuthResponse(result.token(), UserResponse.from(result.user()));
    }
}
