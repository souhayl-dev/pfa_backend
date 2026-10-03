package com.bookingapp.application.catalog;

import com.bookingapp.domain.shared.exception.EntityNotFoundException;
import com.bookingapp.domain.listing.Listing;
import com.bookingapp.domain.provider.Provider;
import com.bookingapp.domain.listing.ListingRepository;
import com.bookingapp.domain.provider.ProviderRepository;

import java.util.UUID;

/**
 * What the public may see: an active, non-deleted listing of an approved provider. Anything else
 * answers "not found", so drafts and suspended businesses are not revealed.
 */
public class PublicListings {

    private final ListingRepository listingRepository;
    private final ProviderRepository providerRepository;

    public PublicListings(ListingRepository listingRepository, ProviderRepository providerRepository) {
        this.listingRepository = listingRepository;
        this.providerRepository = providerRepository;
    }

    public Listing require(UUID listingId) {
        return listingRepository.findById(listingId)
                .filter(Listing::isBookable)
                .filter(listing -> providerRepository.findById(listing.providerId())
                        .map(Provider::isApproved)
                        .orElse(false))
                .orElseThrow(() -> new EntityNotFoundException("Listing", listingId));
    }
}
