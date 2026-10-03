package com.bookingapp.infrastructure.shared.notification;

import com.bookingapp.application.shared.port.EmailSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.nio.charset.StandardCharsets;

/**
 * Sends through the SMTP server set in spring.mail.*. A failure is logged and not passed on: the
 * account exists whether or not its email left, and the user can ask for the message again.
 */
public class SmtpEmailSender implements EmailSender {

    private static final Logger log = LoggerFactory.getLogger(SmtpEmailSender.class);

    private final JavaMailSender mailSender;
    private final AccountEmailTemplates templates;
    private final String from;

    public SmtpEmailSender(JavaMailSender mailSender, AccountEmailTemplates templates, String from) {
        this.mailSender = mailSender;
        this.templates = templates;
        this.from = from;
    }

    @Override
    public void sendEmailVerification(String toEmail, String firstName, String token) {
        send(toEmail, templates.emailVerification(firstName, token));
    }

    @Override
    public void sendPasswordReset(String toEmail, String firstName, String token) {
        send(toEmail, templates.passwordReset(firstName, token));
    }

    private void send(String toEmail, AccountEmail email) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            helper.setFrom(from);
            helper.setTo(toEmail);
            helper.setSubject(email.subject());
            helper.setText(email.text(), email.html());
            mailSender.send(message);
            log.info("Sent \"{}\" to {}", email.subject(), toEmail);
        } catch (MailException | MessagingException e) {
            log.error("Could not send \"{}\" to {}: {}", email.subject(), toEmail, e.getMessage());
        }
    }
}
