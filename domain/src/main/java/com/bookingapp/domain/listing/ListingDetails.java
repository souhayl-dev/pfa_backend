package com.bookingapp.domain.listing;

import com.bookingapp.domain.shared.Require;

import java.math.BigDecimal;
import java.time.LocalTime;

/** The type-specific part of a listing. Exactly one kind of details exists per listing type. */
public sealed interface ListingDetails {

    ListingType type();

    record Hotel(Integer stars, LocalTime checkInTime, LocalTime checkOutTime) implements ListingDetails {
        public Hotel {
            if (stars != null) {
                Require.between(stars, 1, 5, "stars");
            }
        }

        @Override
        public ListingType type() {
            return ListingType.HOTEL;
        }
    }

    record Restaurant(String cuisineType) implements ListingDetails {
        public Restaurant {
            cuisineType = Require.optional(cuisineType, "cuisineType", 50);
        }

        @Override
        public ListingType type() {
            return ListingType.RESTAURANT;
        }
    }

    record Guide(Integer yearsExperience) implements ListingDetails {
        public Guide {
            if (yearsExperience != null) {
                Require.between(yearsExperience, 0, 80, "yearsExperience");
            }
        }

        @Override
        public ListingType type() {
            return ListingType.GUIDE;
        }
    }

    record TravelAgency(String licenseNumber) implements ListingDetails {
        public TravelAgency {
            licenseNumber = Require.notBlank(licenseNumber, "licenseNumber", 100);
        }

        @Override
        public ListingType type() {
            return ListingType.TRAVEL_AGENCY;
        }
    }

    record CarRentalAgency(String licenseNumber, int minDriverAge, BigDecimal depositAmount)
            implements ListingDetails {
        public CarRentalAgency {
            licenseNumber = Require.notBlank(licenseNumber, "licenseNumber", 100);
            Require.between(minDriverAge, 18, 99, "minDriverAge");
            Require.nonNegative(depositAmount, "depositAmount");
        }

        @Override
        public ListingType type() {
            return ListingType.CAR_RENTAL_AGENCY;
        }
    }
}
