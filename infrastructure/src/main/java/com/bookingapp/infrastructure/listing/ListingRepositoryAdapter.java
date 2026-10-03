package com.bookingapp.infrastructure.listing;

import com.bookingapp.domain.listing.Listing;
import com.bookingapp.domain.listing.ListingDetails;
import com.bookingapp.domain.listing.ListingFacets;
import com.bookingapp.domain.listing.ListingRepository;
import com.bookingapp.domain.listing.ListingSearchCriteria;
import com.bookingapp.domain.shared.exception.EntityNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * A listing is stored in two rows: the shared row in listings and the type-specific row in hotels,
 * restaurants, guides, travel_agencies or car_rental_agencies. Both are written in one transaction.
 */
@Repository
public class ListingRepositoryAdapter implements ListingRepository {

    private final ListingJpaRepository listings;
    private final ListingSearchQuery searchQuery;
    private final HotelJpaRepository hotels;
    private final RestaurantJpaRepository restaurants;
    private final GuideJpaRepository guides;
    private final TravelAgencyJpaRepository travelAgencies;
    private final CarRentalAgencyJpaRepository carRentalAgencies;

    public ListingRepositoryAdapter(ListingJpaRepository listings, ListingSearchQuery searchQuery,
                                    HotelJpaRepository hotels,
                                    RestaurantJpaRepository restaurants, GuideJpaRepository guides,
                                    TravelAgencyJpaRepository travelAgencies,
                                    CarRentalAgencyJpaRepository carRentalAgencies) {
        this.listings = listings;
        this.searchQuery = searchQuery;
        this.hotels = hotels;
        this.restaurants = restaurants;
        this.guides = guides;
        this.travelAgencies = travelAgencies;
        this.carRentalAgencies = carRentalAgencies;
    }

    @Override
    @Transactional
    public Listing save(Listing listing) {
        // The shared row must exist first: the type row references it.
        listings.saveAndFlush(ListingMapper.toEntity(listing));
        saveDetails(listing.id(), listing.details());
        return listing;
    }

    private void saveDetails(UUID id, ListingDetails details) {
        switch (details) {
            case ListingDetails.Hotel h -> hotels.save(new HotelJpaEntity(id, h.stars(), h.checkInTime(),
                    h.checkOutTime()));
            case ListingDetails.Restaurant r -> restaurants.save(new RestaurantJpaEntity(id, r.cuisineType()));
            case ListingDetails.Guide g -> guides.save(new GuideJpaEntity(id, g.yearsExperience()));
            case ListingDetails.TravelAgency t -> travelAgencies.save(new TravelAgencyJpaEntity(id,
                    t.licenseNumber()));
            case ListingDetails.CarRentalAgency c -> carRentalAgencies.save(new CarRentalAgencyJpaEntity(id,
                    c.licenseNumber(), c.minDriverAge(), c.depositAmount()));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Listing> findById(UUID id) {
        return listings.findById(id).map(this::toDomain);
    }

    @Override
    @Transactional
    public Optional<Listing> findByIdForUpdate(UUID id) {
        return listings.findByIdForUpdate(id).map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Listing> findByProvider(UUID providerId) {
        return listings.findByProviderIdAndDeletedAtIsNullOrderByCreatedAtAsc(providerId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Listing> search(ListingSearchCriteria criteria) {
        return searchQuery.page(criteria).stream().map(this::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ListingFacets facets(ListingSearchCriteria criteria) {
        return searchQuery.facets(criteria);
    }

    private Listing toDomain(ListingJpaEntity entity) {
        return ListingMapper.toDomain(entity, loadDetails(entity));
    }

    private ListingDetails loadDetails(ListingJpaEntity entity) {
        UUID id = entity.getId();
        Optional<ListingDetails> details = switch (entity.getType()) {
            case HOTEL -> hotels.findById(id).map(ListingMapper::toDomain);
            case RESTAURANT -> restaurants.findById(id).map(ListingMapper::toDomain);
            case GUIDE -> guides.findById(id).map(ListingMapper::toDomain);
            case TRAVEL_AGENCY -> travelAgencies.findById(id).map(ListingMapper::toDomain);
            case CAR_RENTAL_AGENCY -> carRentalAgencies.findById(id).map(ListingMapper::toDomain);
        };
        return details.orElseThrow(() -> new EntityNotFoundException(entity.getType() + " details", id));
    }
}
