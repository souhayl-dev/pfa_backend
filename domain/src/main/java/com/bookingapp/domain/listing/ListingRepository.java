package com.bookingapp.domain.listing;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ListingRepository {
    Listing save(Listing listing);

    Optional<Listing> findById(UUID id);

    /** Loads the listing with a row lock, for updates that must not race (the cached rating). */
    Optional<Listing> findByIdForUpdate(UUID id);

    List<Listing> findByProvider(UUID providerId);

    List<Listing> search(ListingSearchCriteria criteria);

    /** The page and the sort of the criteria play no part. */
    ListingFacets facets(ListingSearchCriteria criteria);
}
