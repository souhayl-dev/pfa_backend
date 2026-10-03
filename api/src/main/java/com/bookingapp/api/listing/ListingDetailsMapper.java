package com.bookingapp.api.listing;

import com.bookingapp.domain.listing.ListingDetails;

/** Converts between the JSON shape of listing details and the domain's ListingDetails. */
final class ListingDetailsMapper {

    private ListingDetailsMapper() {
    }

    static ListingDetails toDomain(HotelDetailsDto dto) {
        return new ListingDetails.Hotel(dto.stars(), dto.checkInTime(), dto.checkOutTime());
    }

    static ListingDetails toDomain(RestaurantDetailsDto dto) {
        return new ListingDetails.Restaurant(dto.cuisineType());
    }

    static ListingDetails toDomain(GuideDetailsDto dto) {
        return new ListingDetails.Guide(dto.yearsExperience());
    }

    static ListingDetails toDomain(TravelAgencyDetailsDto dto) {
        return new ListingDetails.TravelAgency(dto.licenseNumber());
    }

    static ListingDetails toDomain(CarRentalAgencyDetailsDto dto) {
        return new ListingDetails.CarRentalAgency(dto.licenseNumber(), dto.minDriverAge(), dto.depositAmount());
    }

    static HotelDetailsDto hotel(ListingDetails details) {
        return details instanceof ListingDetails.Hotel h
                ? new HotelDetailsDto(h.stars(), h.checkInTime(), h.checkOutTime())
                : null;
    }

    static RestaurantDetailsDto restaurant(ListingDetails details) {
        return details instanceof ListingDetails.Restaurant r ? new RestaurantDetailsDto(r.cuisineType()) : null;
    }

    static GuideDetailsDto guide(ListingDetails details) {
        return details instanceof ListingDetails.Guide g ? new GuideDetailsDto(g.yearsExperience()) : null;
    }

    static TravelAgencyDetailsDto travelAgency(ListingDetails details) {
        return details instanceof ListingDetails.TravelAgency t ? new TravelAgencyDetailsDto(t.licenseNumber()) : null;
    }

    static CarRentalAgencyDetailsDto carRentalAgency(ListingDetails details) {
        return details instanceof ListingDetails.CarRentalAgency c
                ? new CarRentalAgencyDetailsDto(c.licenseNumber(), c.minDriverAge(), c.depositAmount())
                : null;
    }
}
