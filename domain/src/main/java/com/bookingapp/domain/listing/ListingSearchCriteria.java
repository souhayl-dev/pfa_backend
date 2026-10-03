package com.bookingapp.domain.listing;

import java.math.BigDecimal;

/**
 * Public search: only active, non-deleted listings of approved providers are returned. Null means any.
 *
 * <p>text is looked for in the name and the city. Prices compare with the listing's "from" price,
 * the cheapest of its bookable units, so a price filter leaves out listings that have none.
 * minRating leaves out listings that have no review yet.
 */
public record ListingSearchCriteria(ListingType type, String city, String countryCode, String text,
                                    BigDecimal minPrice, BigDecimal maxPrice, BigDecimal minRating,
                                    ListingSort sort, int page, int size) {

    private static final BigDecimal MAX_RATING = BigDecimal.valueOf(5);

    public ListingSearchCriteria {
        city = blankToNull(city);
        countryCode = blankToNull(countryCode);
        text = blankToNull(text);
        sort = sort == null ? ListingSort.RECOMMENDED : sort;
        if (minPrice != null && minPrice.signum() < 0 || maxPrice != null && maxPrice.signum() < 0) {
            throw new IllegalArgumentException("a price cannot be negative");
        }
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("minPrice cannot be above maxPrice");
        }
        if (minRating != null && (minRating.signum() < 0 || minRating.compareTo(MAX_RATING) > 0)) {
            throw new IllegalArgumentException("minRating must be between 0 and 5");
        }
        if (page < 0) {
            throw new IllegalArgumentException("page cannot be negative");
        }
        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("size must be between 1 and 100");
        }
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.strip();
    }
}
