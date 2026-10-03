package com.bookingapp.api.listing;

import com.bookingapp.api.photo.PhotoResponse;
import com.bookingapp.api.unit.UnitResponse;
import com.bookingapp.application.listing.ListingView;
import com.bookingapp.domain.listing.ListingStatus;
import com.bookingapp.domain.listing.ListingType;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/** A listing with everything its page shows. Only the details object matching the type is present. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ListingResponse(UUID id, UUID providerId, String providerName, ListingType type, String name,
                              String description, String address, String city, String countryCode,
                              BigDecimal latitude, BigDecimal longitude, String timezone, String currency,
                              String phone, String email, ListingStatus status, BigDecimal ratingAvg,
                              int reviewsCount, HotelDetailsDto hotel, RestaurantDetailsDto restaurant,
                              GuideDetailsDto guide, TravelAgencyDetailsDto travelAgency,
                              CarRentalAgencyDetailsDto carRentalAgency, List<PhotoResponse> photos,
                              List<UnitResponse> units) {

    public static ListingResponse from(ListingView view) {
        return new ListingResponse(view.id(), view.providerId(), view.providerName(), view.type(), view.name(),
                view.description(), view.address(), view.city(), view.countryCode(), view.latitude(),
                view.longitude(), view.timezone(), view.currency(), view.phone(), view.email(), view.status(),
                view.ratingAvg(), view.reviewsCount(), ListingDetailsMapper.hotel(view.details()),
                ListingDetailsMapper.restaurant(view.details()), ListingDetailsMapper.guide(view.details()),
                ListingDetailsMapper.travelAgency(view.details()),
                ListingDetailsMapper.carRentalAgency(view.details()),
                view.photos().stream().map(PhotoResponse::from).toList(),
                view.units().stream().map(UnitResponse::from).toList());
    }
}
