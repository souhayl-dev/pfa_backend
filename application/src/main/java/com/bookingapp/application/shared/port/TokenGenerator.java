package com.bookingapp.application.shared.port;

public interface TokenGenerator {
    /** A random, unguessable, URL-safe token - used for email verification and password reset links. */
    String generate();
}
