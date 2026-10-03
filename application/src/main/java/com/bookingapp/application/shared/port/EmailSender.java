package com.bookingapp.application.shared.port;

/**
 * The emails an account receives. The use cases say what to send; the adapter writes the message
 * and the link the token travels in.
 */
public interface EmailSender {

    /** Sent at sign-up, and again when the user asks for a new link. */
    void sendEmailVerification(String toEmail, String firstName, String token);

    void sendPasswordReset(String toEmail, String firstName, String token);
}
