package com.bookingapp.application.listing;

import com.bookingapp.application.photo.PhotoView;
import com.bookingapp.application.unit.UnitView;
import com.bookingapp.domain.listing.Listing;
import com.bookingapp.domain.listing.Location;
import com.bookingapp.domain.photo.Photo;
import com.bookingapp.domain.photo.PhotoRepository;
import com.bookingapp.domain.provider.Provider;
import com.bookingapp.domain.provider.ProviderRepository;
import com.bookingapp.domain.unit.BookableUnit;
import com.bookingapp.domain.unit.BookableUnitRepository;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

/** Builds the listing read models shared by the public catalog and the provider dashboard. */
public class ListingViewAssembler {

    private final ProviderRepository providerRepository;
    private final PhotoRepository photoRepository;
    private final BookableUnitRepository unitRepository;

    public ListingViewAssembler(ProviderRepository providerRepository, PhotoRepository photoRepository,
                                BookableUnitRepository unitRepository) {
        this.providerRepository = providerRepository;
        this.photoRepository = photoRepository;
        this.unitRepository = unitRepository;
    }

    /** @param teamView true for the provider's team, who also see inactive units */
    public ListingView full(Listing listing, boolean teamView) {
        Location location = listing.location();
        String providerName = providerRepository.findById(listing.providerId())
                .map(Provider::companyName)
                .orElse(null);
        List<PhotoView> photos = photoRepository.findByListing(listing.id()).stream().map(PhotoView::from).toList();
        List<UnitView> units = unitRepository.findByListing(listing.id()).stream()
                .filter(unit -> teamView || unit.isBookable())
                .map(unit -> UnitView.from(unit, listing.currency(), photoRepository.findByUnit(unit.id())))
                .toList();
        return new ListingView(listing.id(), listing.providerId(), providerName, listing.type(), listing.name(),
                listing.description(), location.address(), location.city(), location.countryCode(),
                location.latitude(), location.longitude(), location.timezone().getId(), listing.currency(),
                listing.phone(), listing.email(), listing.status(), listing.ratingAvg(), listing.reviewsCount(),
                listing.details(), photos, units);
    }

    public ListingSummaryView summary(Listing listing) {
        String cover = photoRepository.findByListing(listing.id()).stream()
                .findFirst()
                .map(Photo::url)
                .orElse(null);
        BigDecimal fromPrice = unitRepository.findByListing(listing.id()).stream()
                .filter(BookableUnit::isBookable)
                .map(BookableUnit::basePrice)
                .min(Comparator.naturalOrder())
                .orElse(null);
        return new ListingSummaryView(listing.id(), listing.providerId(), listing.type(), listing.name(),
                listing.location().city(), listing.location().countryCode(), listing.status(), listing.currency(),
                listing.ratingAvg(), listing.reviewsCount(), cover, fromPrice);
    }
}
