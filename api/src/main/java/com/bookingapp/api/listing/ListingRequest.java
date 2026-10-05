package com.bookingapp.api.listing;

import com.bookingapp.application.listing.ListingCommand;
import com.bookingapp.domain.listing.ListingDetails;
import com.bookingapp.domain.listing.ListingType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Used to create and update a listing. Send the details object matching the type: hotel, restaurant,
 * guide, travelAgency or carRentalAgency. The type cannot change after creation. Prices are in MAD.
 */
public record ListingRequest(
        @NotNull ListingType type,
        @NotBlank @Size(max = 200) String name,
        @Size(max = 4000) String description,
        @Size(max = 255) String address,
        @NotBlank @Size(max = 100) String city,
        @NotBlank @Pattern(regexp = "[A-Z]{2}", message = "must be a 2-letter country code") String countryCode,
        BigDecimal latitude,
        BigDecimal longitude,
        @NotBlank String timezone,
        @Size(max = 30) String phone,
        @Email String email,
        @Valid HotelDetailsDto hotel,
        @Valid RestaurantDetailsDto restaurant,
        @Valid GuideDetailsDto guide,
        @Valid TravelAgencyDetailsDto travelAgency,
        @Valid CarRentalAgencyDetailsDto carRentalAgency) {

    public ListingCommand toCommand() {
        return new ListingCommand(name, description, address, city, countryCode, latitude, longitude, timezone,
                phone, email, details());
    }

    private ListingDetails details() {
        return switch (type) {
            case HOTEL -> ListingDetailsMapper.toDomain(required(hotel));
            case RESTAURANT -> ListingDetailsMapper.toDomain(required(restaurant));
            case GUIDE -> ListingDetailsMapper.toDomain(required(guide));
            case TRAVEL_AGENCY -> ListingDetailsMapper.toDomain(required(travelAgency));
            case CAR_RENTAL_AGENCY -> ListingDetailsMapper.toDomain(required(carRentalAgency));
        };
    }

    private <T> T required(T details) {
        if (details == null) {
            throw new IllegalArgumentException("a " + type + " listing needs its details object");
        }
        return details;
    }
}
