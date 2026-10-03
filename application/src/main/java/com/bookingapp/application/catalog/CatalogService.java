package com.bookingapp.application.catalog;

import com.bookingapp.application.booking.BookingPeriods;
import com.bookingapp.application.booking.BookingPeriods.Period;
import com.bookingapp.application.listing.ListingSummaryView;
import com.bookingapp.application.listing.ListingView;
import com.bookingapp.application.listing.ListingViewAssembler;
import com.bookingapp.domain.booking.AvailabilityPolicy;
import com.bookingapp.domain.booking.BookingRepository;
import com.bookingapp.domain.booking.PricingPolicy;
import com.bookingapp.domain.listing.Listing;
import com.bookingapp.domain.listing.ListingFacets;
import com.bookingapp.domain.listing.ListingRepository;
import com.bookingapp.domain.listing.ListingSearchCriteria;
import com.bookingapp.domain.shared.exception.BookingNotAvailableException;
import com.bookingapp.domain.shared.exception.EntityNotFoundException;
import com.bookingapp.domain.unit.BookableUnit;
import com.bookingapp.domain.unit.BookableUnitRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class CatalogService implements CatalogUseCase {

    private final ListingRepository listingRepository;
    private final BookableUnitRepository unitRepository;
    private final BookingRepository bookingRepository;
    private final PublicListings publicListings;
    private final ListingViewAssembler views;

    public CatalogService(ListingRepository listingRepository, BookableUnitRepository unitRepository,
                          BookingRepository bookingRepository, PublicListings publicListings,
                          ListingViewAssembler views) {
        this.listingRepository = listingRepository;
        this.unitRepository = unitRepository;
        this.bookingRepository = bookingRepository;
        this.publicListings = publicListings;
        this.views = views;
    }

    @Override
    public List<ListingSummaryView> search(ListingSearchCriteria criteria) {
        return listingRepository.search(criteria).stream().map(views::summary).toList();
    }

    @Override
    public ListingFacets facets(ListingSearchCriteria criteria) {
        return listingRepository.facets(criteria);
    }

    @Override
    public ListingView listing(UUID listingId) {
        return views.full(publicListings.require(listingId), false);
    }

    @Override
    public QuoteView quote(UUID unitId, LocalDateTime start, LocalDateTime end, int guestsCount) {
        BookableUnit unit = unitRepository.findById(unitId)
                .filter(BookableUnit::isBookable)
                .orElseThrow(() -> new EntityNotFoundException("Unit", unitId));
        Listing listing = publicListings.require(unit.listingId());
        Period period = BookingPeriods.resolve(listing, unit, start, end);
        var quote = PricingPolicy.quote(unit, period.startAt(), period.endAt(), guestsCount,
                listing.location().timezone());
        String reason = null;
        try {
            AvailabilityPolicy.checkInFuture(unit, period.startAt(), Instant.now());
            AvailabilityPolicy.checkAvailable(unit, period.startAt(), period.endAt(), guestsCount,
                    bookingRepository.findHeld(unitId, period.startAt(), period.endAt()));
        } catch (BookingNotAvailableException e) {
            reason = e.getMessage();
        }
        return new QuoteView(reason == null, reason, period.startAt(), period.endAt(), quote.billedUnits(),
                quote.unitPrice(), quote.total(), listing.currency());
    }
}
