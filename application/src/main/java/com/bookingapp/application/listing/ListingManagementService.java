package com.bookingapp.application.listing;

import com.bookingapp.application.shared.ProviderAccess;
import com.bookingapp.application.shared.port.UnitOfWork;
import com.bookingapp.domain.listing.Listing;
import com.bookingapp.domain.listing.ListingRepository;
import com.bookingapp.domain.listing.Location;
import com.bookingapp.domain.provider.Provider;
import com.bookingapp.domain.provider.ProviderRepository;
import com.bookingapp.domain.shared.exception.BusinessRuleException;
import com.bookingapp.domain.shared.exception.EntityNotFoundException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ListingManagementService implements ListingManagementUseCase {

    private final ListingRepository listingRepository;
    private final ProviderRepository providerRepository;
    private final ProviderAccess access;
    private final ListingViewAssembler views;
    private final UnitOfWork unitOfWork;

    public ListingManagementService(ListingRepository listingRepository, ProviderRepository providerRepository,
                                    ProviderAccess access, ListingViewAssembler views, UnitOfWork unitOfWork) {
        this.listingRepository = listingRepository;
        this.providerRepository = providerRepository;
        this.access = access;
        this.views = views;
        this.unitOfWork = unitOfWork;
    }

    @Override
    public ListingView create(UUID userId, UUID providerId, ListingCommand command) {
        access.member(userId, providerId).requireCanManageListings();
        Listing listing = Listing.draft(UUID.randomUUID(), providerId, command.name(), command.description(),
                location(command), command.currency(), command.phone(), command.email(), command.details(),
                Instant.now());
        return views.full(listingRepository.save(listing), true);
    }

    @Override
    public List<ListingSummaryView> listForProvider(UUID userId, UUID providerId) {
        access.member(userId, providerId);
        return listingRepository.findByProvider(providerId).stream().map(views::summary).toList();
    }

    @Override
    public ListingView get(UUID userId, UUID listingId) {
        return views.full(access.listing(userId, listingId).listing(), true);
    }

    @Override
    public ListingView update(UUID userId, UUID listingId, ListingCommand command) {
        return unitOfWork.inTransaction(() -> {
            Listing listing = access.manageListing(userId, listingId).listing();
            if (command.currency() != null && !command.currency().equals(listing.currency())) {
                throw new BusinessRuleException("the currency of a listing cannot change once it is created");
            }
            Instant now = Instant.now();
            listing.update(command.name(), command.description(), location(command), command.phone(),
                    command.email(), now);
            listing.updateDetails(command.details(), now);
            return views.full(listingRepository.save(listing), true);
        });
    }

    @Override
    public ListingView activate(UUID userId, UUID listingId) {
        Listing listing = access.manageListing(userId, listingId).listing();
        Provider provider = providerRepository.findById(listing.providerId())
                .orElseThrow(() -> new EntityNotFoundException("Provider", listing.providerId()));
        if (!provider.isApproved()) {
            throw new BusinessRuleException("listings go live once the provider is approved");
        }
        listing.activate(Instant.now());
        return views.full(listingRepository.save(listing), true);
    }

    @Override
    public ListingView deactivate(UUID userId, UUID listingId) {
        Listing listing = access.manageListing(userId, listingId).listing();
        listing.deactivate(Instant.now());
        return views.full(listingRepository.save(listing), true);
    }

    @Override
    public void delete(UUID userId, UUID listingId) {
        Listing listing = access.manageListing(userId, listingId).listing();
        listing.softDelete(Instant.now());
        listingRepository.save(listing);
    }

    private static Location location(ListingCommand command) {
        return new Location(command.address(), command.city(), command.countryCode(), command.latitude(),
                command.longitude(), Location.zone(command.timezone()));
    }
}
