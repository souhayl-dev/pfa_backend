package com.bookingapp.application.review;

import com.bookingapp.domain.review.Review;

import java.time.Instant;
import java.util.UUID;

/** authorName is the reviewer's first name only. */
public record ReviewView(UUID id, UUID bookingId, String authorName, int rating, String comment, String reply,
                         Instant repliedAt, Instant createdAt, Instant updatedAt) {

    public static ReviewView from(Review review, String authorName) {
        return new ReviewView(review.id(), review.bookingId(), authorName, review.rating(), review.comment(),
                review.reply(), review.repliedAt(), review.createdAt(), review.updatedAt());
    }
}
