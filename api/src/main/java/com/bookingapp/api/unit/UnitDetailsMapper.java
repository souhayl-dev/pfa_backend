package com.bookingapp.api.unit;

import com.bookingapp.domain.unit.TourStep;
import com.bookingapp.domain.unit.UnitDetails;

/** Converts between the JSON shape of unit details and the domain's UnitDetails. */
final class UnitDetailsMapper {

    private UnitDetailsMapper() {
    }

    static UnitDetails toDomain(RoomDetailsDto dto) {
        return new UnitDetails.Room(dto.roomNumber(), dto.roomType());
    }

    static UnitDetails toDomain(CarDetailsDto dto) {
        return new UnitDetails.Car(dto.brand(), dto.model(), dto.year(), dto.category(), dto.transmission(),
                dto.fuelType(), dto.doors(), dto.hasAc(), dto.plateNumber(), dto.mileageLimitKm());
    }

    static UnitDetails toDomain(TransportDetailsDto dto) {
        return new UnitDetails.Transport(dto.vehicleType());
    }

    static UnitDetails toDomain(TourDetailsDto dto) {
        return new UnitDetails.Tour(dto.durationDays(), dto.steps().stream()
                .map(step -> new TourStep(step.stepOrder(), step.dayNumber(), step.city(), step.description()))
                .toList());
    }

    static RoomDetailsDto room(UnitDetails details) {
        return details instanceof UnitDetails.Room r ? new RoomDetailsDto(r.roomNumber(), r.roomType()) : null;
    }

    static CarDetailsDto car(UnitDetails details) {
        return details instanceof UnitDetails.Car c
                ? new CarDetailsDto(c.brand(), c.model(), c.year(), c.category(), c.transmission(), c.fuelType(),
                        c.doors(), c.hasAc(), c.plateNumber(), c.mileageLimitKm())
                : null;
    }

    static TransportDetailsDto transport(UnitDetails details) {
        return details instanceof UnitDetails.Transport t ? new TransportDetailsDto(t.vehicleType()) : null;
    }

    static TourDetailsDto tour(UnitDetails details) {
        return details instanceof UnitDetails.Tour t
                ? new TourDetailsDto(t.durationDays(), t.steps().stream()
                        .map(step -> new TourStepDto(step.stepOrder(), step.dayNumber(), step.city(),
                                step.description()))
                        .toList())
                : null;
    }
}
