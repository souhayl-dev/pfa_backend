package com.bookingapp.application.listing;

import java.util.List;
import java.util.UUID;

/** The provider team's side of listings. Every method checks the caller's membership and role. */
public interface ListingManagementUseCase {
    ListingView create(UUID userId, UUID providerId, ListingCommand command);

    List<ListingSummaryView> listForProvider(UUID userId, UUID providerId);

    /** Includes drafts and inactive units, which the public cannot see. */
    ListingView get(UUID userId, UUID listingId);

    ListingView update(UUID userId, UUID listingId, ListingCommand command);

    /** Only an approved provider's listing can go live. */
    ListingView activate(UUID userId, UUID listingId);

    ListingView deactivate(UUID userId, UUID listingId);

    void delete(UUID userId, UUID listingId);
}
