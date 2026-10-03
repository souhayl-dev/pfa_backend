package com.bookingapp.application.listing;

import com.bookingapp.application.photo.PhotoView;
import com.bookingapp.application.unit.UnitView;
import com.bookingapp.domain.listing.ListingDetails;
import com.bookingapp.domain.listing.ListingStatus;
import com.bookingapp.domain.listing.ListingType;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/** A listing with everything its page shows: details, photos and units. */
public record ListingView(UUID id, UUID providerId, String providerName, ListingType type, String name,
                          String description, String address, String city, String countryCode, BigDecimal latitude,
                          BigDecimal longitude, String timezone, String currency, String phone, String email,
                          ListingStatus status, BigDecimal ratingAvg, int reviewsCount, ListingDetails details,
                          List<PhotoView> photos, List<UnitView> units) {
}
