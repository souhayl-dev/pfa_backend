package com.bookingapp.domain.shared;

/**
 * Every price on the platform is in Moroccan dirhams. Listings and bookings still store the code
 * beside their amounts, so an amount is never read without its currency.
 */
public final class PlatformCurrency {

    public static final String CODE = "MAD";

    private PlatformCurrency() {
    }
}
