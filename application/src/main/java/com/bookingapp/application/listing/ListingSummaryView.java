package com.bookingapp.application.listing;

import com.bookingapp.domain.listing.ListingStatus;
import com.bookingapp.domain.listing.ListingType;

import java.math.BigDecimal;
import java.util.UUID;

/** A card in search results or in a provider's list. fromPrice is the cheapest bookable unit, if any. */
public record ListingSummaryView(UUID id, UUID providerId, ListingType type, String name, String city,
                                 String countryCode, ListingStatus status, String currency, BigDecimal ratingAvg,
                                 int reviewsCount, String coverPhotoUrl, BigDecimal fromPrice) {
}
