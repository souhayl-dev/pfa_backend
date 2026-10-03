package com.bookingapp.domain.listing;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ListingSearchCriteriaTest {

    private static ListingSearchCriteria criteria(String text, String minPrice, String maxPrice, String minRating,
                                                  ListingSort sort) {
        return new ListingSearchCriteria(null, " ", null, text, decimal(minPrice), decimal(maxPrice),
                decimal(minRating), sort, 0, 20);
    }

    private static BigDecimal decimal(String value) {
        return value == null ? null : new BigDecimal(value);
    }

    @Test
    void blankFiltersMeanAnyAndTheDefaultSortIsRecommended() {
        ListingSearchCriteria criteria = criteria("  riad ", null, null, null, null);

        assertNull(criteria.city());
        assertEquals("riad", criteria.text());
        assertEquals(ListingSort.RECOMMENDED, criteria.sort());
    }

    @Test
    void rejectsPriceRangeUpsideDown() {
        var error = assertThrows(IllegalArgumentException.class, () -> criteria(null, "90", "40", null, null));
        assertEquals("minPrice cannot be above maxPrice", error.getMessage());
    }

    @Test
    void rejectsNegativePrice() {
        assertThrows(IllegalArgumentException.class, () -> criteria(null, "-1", null, null, null));
    }

    @Test
    void rejectsRatingAboveFive() {
        var error = assertThrows(IllegalArgumentException.class, () -> criteria(null, null, null, "5.5", null));
        assertEquals("minRating must be between 0 and 5", error.getMessage());
    }
}
