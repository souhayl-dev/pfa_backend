package com.bookingapp.application.catalog;

import com.bookingapp.application.listing.ListingSummaryView;
import com.bookingapp.application.listing.ListingView;
import com.bookingapp.domain.listing.ListingFacets;
import com.bookingapp.domain.listing.ListingSearchCriteria;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/** What anyone, signed in or not, can read: active listings of approved providers. */
public interface CatalogUseCase {
    List<ListingSummaryView> search(ListingSearchCriteria criteria);

    /** The counts shown beside the filters of a search. */
    ListingFacets facets(ListingSearchCriteria criteria);

    ListingView listing(UUID listingId);

    /** start and end are in the listing's local time; see BookingPeriods for how each unit type uses them. */
    QuoteView quote(UUID unitId, LocalDateTime start, LocalDateTime end, int guestsCount);
}
