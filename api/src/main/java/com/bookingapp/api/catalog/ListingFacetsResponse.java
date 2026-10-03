package com.bookingapp.api.catalog;

import com.bookingapp.domain.listing.ListingFacets;
import com.bookingapp.domain.listing.ListingType;

import java.math.BigDecimal;
import java.util.Map;

/**
 * total matches every filter. types, cities and the price bounds each ignore their own filter and
 * apply the others, so a search page can show what every choice would give. minPrice and maxPrice
 * are null when no matching listing has a price.
 */
public record ListingFacetsResponse(long total, Map<ListingType, Long> types, Map<String, Long> cities,
                                    BigDecimal minPrice, BigDecimal maxPrice) {

    public static ListingFacetsResponse from(ListingFacets facets) {
        return new ListingFacetsResponse(facets.total(), facets.types(), facets.cities(), facets.minPrice(),
                facets.maxPrice());
    }
}
