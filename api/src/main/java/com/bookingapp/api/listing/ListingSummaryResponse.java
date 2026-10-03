package com.bookingapp.api.listing;

import com.bookingapp.application.listing.ListingSummaryView;
import com.bookingapp.domain.listing.ListingStatus;
import com.bookingapp.domain.listing.ListingType;

import java.math.BigDecimal;
import java.util.UUID;

/** A card in search results or in a provider's list. fromPrice is the cheapest bookable unit, if any. */
public record ListingSummaryResponse(UUID id, UUID providerId, ListingType type, String name, String city,
                                     String countryCode, ListingStatus status, String currency,
                                     BigDecimal ratingAvg, int reviewsCount, String coverPhotoUrl,
                                     BigDecimal fromPrice) {

    public static ListingSummaryResponse from(ListingSummaryView view) {
        return new ListingSummaryResponse(view.id(), view.providerId(), view.type(), view.name(), view.city(),
                view.countryCode(), view.status(), view.currency(), view.ratingAvg(), view.reviewsCount(),
                view.coverPhotoUrl(), view.fromPrice());
    }
}
