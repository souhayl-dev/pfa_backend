package com.bookingapp.infrastructure.unit;

import com.bookingapp.domain.unit.BookableUnit;
import com.bookingapp.domain.unit.TourStep;
import com.bookingapp.domain.unit.UnitDetails;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class BookableUnitMapper {

    private BookableUnitMapper() {
    }

    /** details is null for tables and guide services, which have no details table. */
    public static BookableUnit toDomain(BookableUnitJpaEntity e, UnitDetails details) {
        return new BookableUnit(e.getId(), e.getListingId(), e.getType(), e.getName(), e.getDescription(),
                e.getBasePrice(), e.getCapacity(), details, e.isActive(), e.getCreatedAt(), e.getUpdatedAt(),
                e.getDeletedAt());
    }

    public static BookableUnitJpaEntity toEntity(BookableUnit u) {
        return new BookableUnitJpaEntity(u.id(), u.listingId(), u.type(), u.name(), u.description(), u.basePrice(),
                u.capacity(), u.active(), u.createdAt(), u.updatedAt(), u.deletedAt());
    }

    public static UnitDetails toDomain(RoomJpaEntity e) {
        return new UnitDetails.Room(e.getRoomNumber(), e.getRoomType());
    }

    public static RoomJpaEntity toEntity(UUID unitId, UnitDetails.Room r) {
        return new RoomJpaEntity(unitId, r.roomNumber(), r.roomType());
    }

    public static UnitDetails toDomain(CarJpaEntity e) {
        return new UnitDetails.Car(e.getBrand(), e.getModel(), e.getYear(), e.getCategory(), e.getTransmission(),
                e.getFuelType(), e.getDoors(), e.isHasAc(), e.getPlateNumber(), e.getMileageLimitKm());
    }

    public static CarJpaEntity toEntity(UUID unitId, UnitDetails.Car c) {
        return new CarJpaEntity(unitId, c.brand(), c.model(), c.year(), c.category(), c.transmission(), c.fuelType(),
                c.doors(), c.hasAc(), c.plateNumber(), c.mileageLimitKm());
    }

    public static UnitDetails toDomain(TransportJpaEntity e) {
        return new UnitDetails.Transport(e.getVehicleType());
    }

    public static TransportJpaEntity toEntity(UUID unitId, UnitDetails.Transport t) {
        return new TransportJpaEntity(unitId, t.vehicleType());
    }

    public static UnitDetails toDomain(TourJpaEntity e) {
        List<TourStep> steps = e.getSteps().stream()
                .map(s -> new TourStep(s.getStepOrder(), s.getDayNumber(), s.getCity(), s.getDescription()))
                .toList();
        return new UnitDetails.Tour(e.getDurationDays(), steps);
    }

    public static TourJpaEntity toEntity(UUID unitId, UnitDetails.Tour t) {
        Set<TourStepEmbeddable> steps = t.steps().stream()
                .map(s -> new TourStepEmbeddable(s.stepOrder(), s.dayNumber(), s.city(), s.description()))
                .collect(Collectors.toSet());
        return new TourJpaEntity(unitId, t.durationDays(), steps);
    }
}
