package com.bookingapp.application.shared.port;

import com.bookingapp.domain.user.UserRole;

import java.util.Set;
import java.util.UUID;

public interface TokenIssuer {
    /** Provider access is checked per request against memberships, so only platform roles go in the token. */
    String issueToken(UUID userId, Set<UserRole> roles);
}
