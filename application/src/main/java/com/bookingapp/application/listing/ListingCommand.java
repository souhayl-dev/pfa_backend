package com.bookingapp.application.listing;

import com.bookingapp.domain.listing.ListingDetails;

import java.math.BigDecimal;

/**
 * Used to create and update a listing. The type is the type of the details and cannot change;
 * currency is set at creation and cannot change either.
 */
public record ListingCommand(String name, String description, String address, String city, String countryCode,
                             BigDecimal latitude, BigDecimal longitude, String timezone, String currency,
                             String phone, String email, ListingDetails details) {
}
