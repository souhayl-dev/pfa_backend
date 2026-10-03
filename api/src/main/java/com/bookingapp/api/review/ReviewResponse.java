package com.bookingapp.api.review;

import com.bookingapp.application.review.ReviewView;

import java.time.Instant;
import java.util.UUID;

/** authorName is the reviewer's first name only. reply and repliedAt are null until the provider replies. */
public record ReviewResponse(UUID id, UUID bookingId, String authorName, int rating, String comment, String reply,
                             Instant repliedAt, Instant createdAt, Instant updatedAt) {

    public static ReviewResponse from(ReviewView view) {
        return new ReviewResponse(view.id(), view.bookingId(), view.authorName(), view.rating(), view.comment(),
                view.reply(), view.repliedAt(), view.createdAt(), view.updatedAt());
    }
}
