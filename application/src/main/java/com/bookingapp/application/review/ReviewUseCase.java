package com.bookingapp.application.review;

import java.util.List;
import java.util.UUID;

public interface ReviewUseCase {
    /** Public: the reviews of a listing the public can see, newest first. */
    List<ReviewView> ofListing(UUID listingId);

    /** Only the client of a completed booking, once per booking. */
    ReviewView write(UUID userId, UUID bookingId, int rating, String comment);

    ReviewView edit(UUID userId, UUID reviewId, int rating, String comment);

    /** Any active member of the listing's provider can reply. */
    ReviewView reply(UUID userId, UUID reviewId, String text);
}
