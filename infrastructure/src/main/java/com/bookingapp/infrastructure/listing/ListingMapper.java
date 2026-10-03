package com.bookingapp.infrastructure.listing;

import com.bookingapp.domain.listing.Listing;
import com.bookingapp.domain.listing.ListingDetails;
import com.bookingapp.domain.listing.Location;

import java.time.ZoneId;

public final class ListingMapper {

    private ListingMapper() {
    }

    /** The details come from the type table matching the listing's type. */
    public static Listing toDomain(ListingJpaEntity e, ListingDetails details) {
        Location location = new Location(e.getAddress(), e.getCity(), e.getCountryCode(), e.getLatitude(),
                e.getLongitude(), ZoneId.of(e.getTimezone()));
        return new Listing(e.getId(), e.getProviderId(), e.getName(), e.getDescription(), location, e.getCurrency(),
                e.getPhone(), e.getEmail(), e.getStatus(), details, e.getRatingAvg(), e.getReviewsCount(),
                e.getCreatedAt(), e.getUpdatedAt(), e.getDeletedAt());
    }

    public static ListingJpaEntity toEntity(Listing l) {
        Location location = l.location();
        return new ListingJpaEntity(l.id(), l.providerId(), l.type(), l.name(), l.description(), location.address(),
                location.city(), location.countryCode(), location.latitude(), location.longitude(),
                location.timezone().getId(), l.currency(), l.phone(), l.email(), l.status(), l.ratingAvg(),
                l.reviewsCount(), l.createdAt(), l.updatedAt(), l.deletedAt());
    }

    public static ListingDetails toDomain(HotelJpaEntity e) {
        return new ListingDetails.Hotel(e.getStars(), e.getCheckInTime(), e.getCheckOutTime());
    }

    public static ListingDetails toDomain(RestaurantJpaEntity e) {
        return new ListingDetails.Restaurant(e.getCuisineType());
    }

    public static ListingDetails toDomain(GuideJpaEntity e) {
        return new ListingDetails.Guide(e.getYearsExperience());
    }

    public static ListingDetails toDomain(TravelAgencyJpaEntity e) {
        return new ListingDetails.TravelAgency(e.getLicenseNumber());
    }

    public static ListingDetails toDomain(CarRentalAgencyJpaEntity e) {
        return new ListingDetails.CarRentalAgency(e.getLicenseNumber(), e.getMinDriverAge(), e.getDepositAmount());
    }
}
