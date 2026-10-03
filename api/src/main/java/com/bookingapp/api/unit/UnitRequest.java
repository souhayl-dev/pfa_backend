package com.bookingapp.api.unit;

import com.bookingapp.application.unit.UnitCommand;
import com.bookingapp.domain.unit.UnitDetails;
import com.bookingapp.domain.unit.UnitType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Used to create and update a unit. Send the details object matching the type: room, car, transport
 * or tour. Tables and guide services send none. capacity is the number of people the unit takes.
 */
public record UnitRequest(
        @NotNull UnitType type,
        @NotBlank @Size(max = 200) String name,
        @Size(max = 2000) String description,
        @NotNull @PositiveOrZero BigDecimal basePrice,
        @Positive int capacity,
        @Valid RoomDetailsDto room,
        @Valid CarDetailsDto car,
        @Valid TransportDetailsDto transport,
        @Valid TourDetailsDto tour) {

    public UnitCommand toCommand() {
        return new UnitCommand(type, name, description, basePrice, capacity, details());
    }

    private UnitDetails details() {
        return switch (type) {
            case ROOM -> UnitDetailsMapper.toDomain(required(room));
            case CAR -> UnitDetailsMapper.toDomain(required(car));
            case TRANSPORT -> UnitDetailsMapper.toDomain(required(transport));
            case TOUR -> UnitDetailsMapper.toDomain(required(tour));
            case TABLE, GUIDE_SERVICE -> null;
        };
    }

    private <T> T required(T details) {
        if (details == null) {
            throw new IllegalArgumentException("a " + type + " unit needs its details object");
        }
        return details;
    }
}
