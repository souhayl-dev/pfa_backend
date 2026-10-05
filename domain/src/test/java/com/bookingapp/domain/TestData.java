package com.bookingapp.domain;

import com.bookingapp.domain.booking.Booking;
import com.bookingapp.domain.booking.BookingStatus;
import com.bookingapp.domain.listing.Listing;
import com.bookingapp.domain.listing.ListingDetails;
import com.bookingapp.domain.listing.Location;
import com.bookingapp.domain.shared.Money;
import com.bookingapp.domain.unit.BookableUnit;
import com.bookingapp.domain.unit.TourStep;
import com.bookingapp.domain.unit.UnitDetails;
import com.bookingapp.domain.unit.UnitType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

public final class TestData {

    public static final ZoneId MARRAKECH = ZoneId.of("Africa/Casablanca");
    public static final Instant NOW = Instant.parse("2026-09-30T10:00:00Z");

    private TestData() {
    }

    public static Location marrakech() {
        return new Location("12 Derb El Hammam", "Marrakech", "MA", null, null, MARRAKECH);
    }

    private static Listing active(String name, ListingDetails details) {
        Listing listing = Listing.draft(UUID.randomUUID(), UUID.randomUUID(), name, null, marrakech(), null,
                null, details, NOW);
        listing.activate(NOW);
        return listing;
    }

    public static Listing activeHotel() {
        return active("Riad Jardin Secret", new ListingDetails.Hotel(5, LocalTime.of(14, 0), LocalTime.of(12, 0)));
    }

    public static Listing activeRestaurant() {
        return active("La Terrasse des Epices", new ListingDetails.Restaurant("MOROCCAN"));
    }

    public static Listing activeTravelAgency() {
        return active("Fatima Excursions", new ListingDetails.TravelAgency("AGV-1"));
    }

    public static Listing activeCarAgency() {
        return active("Youssef Location", new ListingDetails.CarRentalAgency("LOC-1", 21, BigDecimal.ZERO));
    }

    public static BookableUnit room(Listing hotel) {
        return BookableUnit.create(UUID.randomUUID(), hotel, UnitType.ROOM, "Patio Double Room", null,
                new BigDecimal("85.00"), 2, new UnitDetails.Room("101", UnitDetails.RoomType.DOUBLE), NOW);
    }

    public static BookableUnit car(Listing agency) {
        return BookableUnit.create(UUID.randomUUID(), agency, UnitType.CAR, "Dacia Duster", null,
                new BigDecimal("45.00"), 5, new UnitDetails.Car("Dacia", "Duster", 2023, UnitDetails.CarCategory.SUV,
                        UnitDetails.Transmission.AUTOMATIC, UnitDetails.FuelType.DIESEL, 5, true, "12345-A-6", 300),
                NOW);
    }

    public static BookableUnit table(Listing restaurant, int seats) {
        return BookableUnit.create(UUID.randomUUID(), restaurant, UnitType.TABLE, "Terrace seating", null,
                new BigDecimal("25.00"), seats, null, NOW);
    }

    public static BookableUnit tour(Listing agency, int groupSize) {
        return BookableUnit.create(UUID.randomUUID(), agency, UnitType.TOUR, "Atlas and Valleys", null,
                new BigDecimal("180.00"), groupSize,
                new UnitDetails.Tour(3, List.of(new TourStep(1, 1, "Ait Benhaddou", null),
                        new TourStep(2, 2, "Ouarzazate", null), new TourStep(3, 3, "Marrakech", null))), NOW);
    }

    /** A confirmed booking of the unit, as the repository would return it. */
    public static Booking held(BookableUnit unit, String start, String end, int guests) {
        return new Booking(UUID.randomUUID(), "BK-7Q2M4X", UUID.randomUUID(), unit.id(), BookingStatus.CONFIRMED,
                Instant.parse(start), Instant.parse(end), guests, unit.basePrice(),
                Money.of(unit.basePrice(), "MAD"), null, NOW, NOW);
    }
}
