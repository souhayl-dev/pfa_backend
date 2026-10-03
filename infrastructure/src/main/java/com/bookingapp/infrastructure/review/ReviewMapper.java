package com.bookingapp.infrastructure.review;

import com.bookingapp.domain.review.Review;

public final class ReviewMapper {

    private ReviewMapper() {
    }

    public static Review toDomain(ReviewJpaEntity e) {
        return new Review(e.getId(), e.getBookingId(), e.getRating(), e.getComment(), e.getReply(), e.getRepliedBy(),
                e.getRepliedAt(), e.getCreatedAt(), e.getUpdatedAt());
    }

    public static ReviewJpaEntity toEntity(Review r) {
        return new ReviewJpaEntity(r.id(), r.bookingId(), r.rating(), r.comment(), r.reply(), r.repliedBy(),
                r.repliedAt(), r.createdAt(), r.updatedAt());
    }
}
