package com.bookingapp.domain.listing;

import java.math.BigDecimal;
import java.util.Map;

/**
 * What the filters of a search would give, so the search page can show a count beside each choice.
 *
 * <p>total is the number of listings matching every filter. Each of the other values ignores its own
 * filter and applies all the others: types ignores the type, cities ignores the city, and the price
 * bounds ignore the price range. The bounds are null when no matching listing has a price.
 */
public record ListingFacets(long total, Map<ListingType, Long> types, Map<String, Long> cities,
                            BigDecimal minPrice, BigDecimal maxPrice) {
}
