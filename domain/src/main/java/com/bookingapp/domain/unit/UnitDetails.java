package com.bookingapp.domain.unit;

import com.bookingapp.domain.shared.Require;

import java.util.Comparator;
import java.util.List;

/** The type-specific part of a bookable unit. Tables and guide services have none. */
public sealed interface UnitDetails {

    UnitType type();

    enum RoomType { SINGLE, DOUBLE, TWIN, TRIPLE, SUITE, FAMILY }

    enum CarCategory { ECONOMY, COMPACT, SUV, LUXURY, VAN }

    enum Transmission { MANUAL, AUTOMATIC }

    enum FuelType { PETROL, DIESEL, HYBRID, ELECTRIC }

    enum VehicleType { CAR, VAN, MINIBUS, BUS, FOUR_BY_FOUR }

    record Room(String roomNumber, RoomType roomType) implements UnitDetails {
        public Room {
            roomNumber = Require.notBlank(roomNumber, "roomNumber", 20);
            Require.notNull(roomType, "roomType");
        }

        @Override
        public UnitType type() {
            return UnitType.ROOM;
        }
    }

    /** The number of seats is the unit's capacity. */
    record Car(String brand, String model, int year, CarCategory category, Transmission transmission,
               FuelType fuelType, int doors, boolean hasAc, String plateNumber, Integer mileageLimitKm)
            implements UnitDetails {
        public Car {
            brand = Require.notBlank(brand, "brand", 50);
            model = Require.notBlank(model, "model", 50);
            Require.between(year, 1950, 2100, "year");
            Require.notNull(category, "category");
            Require.notNull(transmission, "transmission");
            Require.notNull(fuelType, "fuelType");
            Require.between(doors, 1, 10, "doors");
            plateNumber = Require.notBlank(plateNumber, "plateNumber", 20).toUpperCase();
            if (mileageLimitKm != null) {
                Require.positive(mileageLimitKm, "mileageLimitKm");
            }
        }

        @Override
        public UnitType type() {
            return UnitType.CAR;
        }
    }

    /** A vehicle with a driver, run by a travel agency. Self-drive rentals are cars. */
    record Transport(VehicleType vehicleType) implements UnitDetails {
        public Transport {
            Require.notNull(vehicleType, "vehicleType");
        }

        @Override
        public UnitType type() {
            return UnitType.TRANSPORT;
        }
    }

    /**
     * A multi-day trip. The unit's base price is per person and its capacity is the group size.
     * Steps are numbered 1..n without gaps, in day order, and each falls within the tour's days.
     */
    record Tour(int durationDays, List<TourStep> steps) implements UnitDetails {
        public Tour {
            Require.positive(durationDays, "durationDays");
            Require.notNull(steps, "steps");
            List<TourStep> sorted = steps.stream().sorted(Comparator.comparingInt(TourStep::stepOrder)).toList();
            for (int i = 0; i < sorted.size(); i++) {
                TourStep step = sorted.get(i);
                if (step.stepOrder() != i + 1) {
                    throw new IllegalArgumentException("tour steps must be numbered 1, 2, 3... without gaps");
                }
                if (step.dayNumber() > durationDays) {
                    throw new IllegalArgumentException("step " + step.stepOrder() + " is on day "
                            + step.dayNumber() + " but the tour lasts " + durationDays + " days");
                }
                if (i > 0 && step.dayNumber() < sorted.get(i - 1).dayNumber()) {
                    throw new IllegalArgumentException("tour steps must be in day order");
                }
            }
            steps = sorted;
        }

        @Override
        public UnitType type() {
            return UnitType.TOUR;
        }
    }
}
