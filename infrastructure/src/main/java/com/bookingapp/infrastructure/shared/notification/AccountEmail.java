package com.bookingapp.infrastructure.shared.notification;

/** A message ready to send: the same content as plain text and as HTML. */
public record AccountEmail(String subject, String text, String html) {
}
