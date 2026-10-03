package com.bookingapp.infrastructure.shared.notification;

import com.bookingapp.application.shared.port.EmailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used when no mail server is configured: the message is written to the log instead of being sent,
 * so its link can still be opened during development.
 */
public class ConsoleEmailSender implements EmailSender {

    private static final Logger log = LoggerFactory.getLogger(ConsoleEmailSender.class);

    private final AccountEmailTemplates templates;

    public ConsoleEmailSender(AccountEmailTemplates templates) {
        this.templates = templates;
    }

    @Override
    public void sendEmailVerification(String toEmail, String firstName, String token) {
        print(toEmail, templates.emailVerification(firstName, token));
    }

    @Override
    public void sendPasswordReset(String toEmail, String firstName, String token) {
        print(toEmail, templates.passwordReset(firstName, token));
    }

    private static void print(String toEmail, AccountEmail email) {
        log.info("---- EMAIL (no mail server configured, not sent) ----\nTo: {}\nSubject: {}\n{}----------------------",
                toEmail, email.subject(), email.text());
    }
}
