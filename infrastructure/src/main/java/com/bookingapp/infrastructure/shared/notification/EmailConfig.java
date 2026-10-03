package com.bookingapp.infrastructure.shared.notification;

import com.bookingapp.application.shared.port.EmailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

/** Real emails once a mail server is configured (MAIL_HOST); the log until then. */
@Configuration
public class EmailConfig {

    private static final Logger log = LoggerFactory.getLogger(EmailConfig.class);

    @Bean
    public EmailSender emailSender(ObjectProvider<JavaMailSender> mailSender, AccountEmailTemplates templates,
                                   @Value("${spring.mail.host:}") String host,
                                   @Value("${app.mail.from}") String from) {
        JavaMailSender smtp = mailSender.getIfAvailable();
        if (host.isBlank() || smtp == null) {
            log.info("No mail server configured: emails are written to this log");
            return new ConsoleEmailSender(templates);
        }
        log.info("Emails are sent through {}", host);
        return new SmtpEmailSender(smtp, templates, from);
    }
}
