package com.bookingapp.domain.listing;

/** The orders the public search can return its results in. */
public enum ListingSort {
    /** Best rated first, a rating counting for more once it rests on several reviews. */
    RECOMMENDED,
    /** By the price of the cheapest bookable unit; listings without one come last. */
    PRICE_ASC,
    PRICE_DESC,
    RATING,
    NAME
}
