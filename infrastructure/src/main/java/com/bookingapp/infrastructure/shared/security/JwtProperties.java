package com.bookingapp.infrastructure.shared.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "booking.jwt")
public class JwtProperties {

    /** Must be at least 256 bits (32 chars) for HS256; set via the BOOKING_JWT_SECRET env var in real deployments. */
    private String secret = "change-this-development-only-secret-key-please";

    private long expirationMinutes = 60 * 24;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getExpirationMinutes() {
        return expirationMinutes;
    }

    public void setExpirationMinutes(long expirationMinutes) {
        this.expirationMinutes = expirationMinutes;
    }
}
