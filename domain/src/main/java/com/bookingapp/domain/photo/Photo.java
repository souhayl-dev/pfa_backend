package com.bookingapp.domain.photo;

import com.bookingapp.domain.shared.Require;

import java.time.Instant;
import java.util.UUID;

/** A photo of a listing or of a unit: exactly one of listingId and unitId is set. */
public record Photo(UUID id, UUID listingId, UUID unitId, String url, int sortOrder, Instant createdAt) {

    public Photo {
        Require.notNull(id, "id");
        if ((listingId == null) == (unitId == null)) {
            throw new IllegalArgumentException("a photo belongs to a listing or to a unit, not both");
        }
        url = Require.notBlank(url, "url", 500);
        Require.notNull(createdAt, "createdAt");
    }

    public static Photo ofListing(UUID id, UUID listingId, String url, int sortOrder, Instant now) {
        return new Photo(id, Require.notNull(listingId, "listingId"), null, url, sortOrder, now);
    }

    public static Photo ofUnit(UUID id, UUID unitId, String url, int sortOrder, Instant now) {
        return new Photo(id, null, Require.notNull(unitId, "unitId"), url, sortOrder, now);
    }

    public boolean isUnitPhoto() {
        return unitId != null;
    }
}
