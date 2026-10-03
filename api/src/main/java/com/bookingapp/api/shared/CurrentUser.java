package com.bookingapp.api.shared;

import com.bookingapp.infrastructure.shared.security.AuthenticatedUser;
import org.springframework.security.core.Authentication;

import java.util.UUID;

/** Reads the signed-in user from the security context filled by the JWT filter. */
public final class CurrentUser {

    private CurrentUser() {
    }

    public static UUID id(Authentication authentication) {
        return ((AuthenticatedUser) authentication.getPrincipal()).userId();
    }
}
