package com.bookingapp.domain.user;

import java.time.Duration;

public enum UserTokenType {
    REFRESH(Duration.ofDays(30)),
    VERIFY_EMAIL(Duration.ofDays(2)),
    RESET_PASSWORD(Duration.ofHours(1));

    private final Duration lifetime;

    UserTokenType(Duration lifetime) {
        this.lifetime = lifetime;
    }

    public Duration lifetime() {
        return lifetime;
    }
}
