package com.bookingapp.domain.review;

import com.bookingapp.domain.shared.exception.BusinessRuleException;
import com.bookingapp.domain.shared.Require;
import com.bookingapp.domain.booking.Booking;

import java.time.Instant;
import java.util.UUID;

/** One review per completed booking. The client and the listing are read through the booking. */
public class Review {

    private final UUID id;
    private final UUID bookingId;
    private int rating;
    private String comment;
    private String reply;
    private UUID repliedBy;
    private Instant repliedAt;
    private final Instant createdAt;
    private Instant updatedAt;

    public Review(UUID id, UUID bookingId, int rating, String comment, String reply, UUID repliedBy,
                  Instant repliedAt, Instant createdAt, Instant updatedAt) {
        this.id = Require.notNull(id, "id");
        this.bookingId = Require.notNull(bookingId, "bookingId");
        this.rating = Require.between(rating, 1, 5, "rating");
        this.comment = Require.optional(comment, "comment", 2000);
        boolean hasReply = reply != null;
        if (hasReply != (repliedBy != null) || hasReply != (repliedAt != null)) {
            throw new IllegalArgumentException("a reply needs its text, author and time together");
        }
        this.reply = Require.optional(reply, "reply", 2000);
        this.repliedBy = repliedBy;
        this.repliedAt = repliedAt;
        this.createdAt = Require.notNull(createdAt, "createdAt");
        this.updatedAt = Require.notNull(updatedAt, "updatedAt");
    }

    public static Review write(UUID id, Booking booking, int rating, String comment, Instant now) {
        if (!booking.isCompleted()) {
            throw new BusinessRuleException("you can review a booking once it is completed");
        }
        return new Review(id, booking.id(), rating, comment, null, null, null, now, now);
    }

    public void edit(int rating, String comment, Instant now) {
        this.rating = Require.between(rating, 1, 5, "rating");
        this.comment = Require.optional(comment, "comment", 2000);
        this.updatedAt = now;
    }

    /** The caller must have checked that the author belongs to the listing's provider team. */
    public void reply(String text, UUID authorId, Instant now) {
        this.reply = Require.notBlank(text, "reply", 2000);
        this.repliedBy = Require.notNull(authorId, "authorId");
        this.repliedAt = now;
        this.updatedAt = now;
    }

    public UUID id() {
        return id;
    }

    public UUID bookingId() {
        return bookingId;
    }

    public int rating() {
        return rating;
    }

    public String comment() {
        return comment;
    }

    public String reply() {
        return reply;
    }

    public UUID repliedBy() {
        return repliedBy;
    }

    public Instant repliedAt() {
        return repliedAt;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
