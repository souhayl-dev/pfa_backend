package com.bookingapp.application.listing;

import com.bookingapp.domain.listing.ListingDetails;

import java.math.BigDecimal;

/**
 * Used to create and update a listing. The type is the type of the details and cannot change.
 */
public record ListingCommand(String name, String description, String address, String city, String countryCode,
                             BigDecimal latitude, BigDecimal longitude, String timezone,
                             String phone, String email, ListingDetails details) {
}
