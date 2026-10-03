package com.bookingapp.application.photo;

import java.util.UUID;

/** Photos of listings and units. Needs an owner or manager of the listing's provider. */
public interface PhotoUseCase {
    PhotoView addToListing(UUID userId, UUID listingId, String url);

    PhotoView addToUnit(UUID userId, UUID unitId, String url);

    void remove(UUID userId, UUID photoId);
}
