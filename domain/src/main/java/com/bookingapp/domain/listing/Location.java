package com.bookingapp.domain.listing;

import com.bookingapp.domain.shared.Require;

import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.ZoneId;

/** Where a listing is. The address is optional because a guide has no fixed address. */
public record Location(String address, String city, String countryCode, BigDecimal latitude, BigDecimal longitude,
                       ZoneId timezone) {

    private static final BigDecimal MAX_LATITUDE = BigDecimal.valueOf(90);
    private static final BigDecimal MAX_LONGITUDE = BigDecimal.valueOf(180);

    public Location {
        address = Require.optional(address, "address", 255);
        city = Require.notBlank(city, "city", 100);
        countryCode = Require.countryCode(countryCode, "countryCode");
        Require.notNull(timezone, "timezone");
        if ((latitude == null) != (longitude == null)) {
            throw new IllegalArgumentException("latitude and longitude must be given together");
        }
        if (latitude != null && latitude.abs().compareTo(MAX_LATITUDE) > 0) {
            throw new IllegalArgumentException("latitude must be between -90 and 90");
        }
        if (longitude != null && longitude.abs().compareTo(MAX_LONGITUDE) > 0) {
            throw new IllegalArgumentException("longitude must be between -180 and 180");
        }
    }

    public static ZoneId zone(String timezone) {
        try {
            return ZoneId.of(Require.notBlank(timezone, "timezone", 64));
        } catch (DateTimeException e) {
            throw new IllegalArgumentException("unknown timezone: " + timezone);
        }
    }
}
