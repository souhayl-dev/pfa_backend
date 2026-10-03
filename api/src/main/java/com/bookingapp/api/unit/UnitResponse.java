package com.bookingapp.api.unit;

import com.bookingapp.api.photo.PhotoResponse;
import com.bookingapp.application.unit.UnitView;
import com.bookingapp.domain.unit.UnitType;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * pricingUnit says what basePrice is charged per: PER_NIGHT, PER_DAY, PER_TRIP or PER_PERSON.
 * Only the details object matching the type is present.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UnitResponse(UUID id, UUID listingId, UnitType type, UnitType.PricingUnit pricingUnit, String name,
                           String description, BigDecimal basePrice, String currency, int capacity, boolean active,
                           RoomDetailsDto room, CarDetailsDto car, TransportDetailsDto transport,
                           TourDetailsDto tour, List<PhotoResponse> photos) {

    public static UnitResponse from(UnitView view) {
        return new UnitResponse(view.id(), view.listingId(), view.type(), view.pricingUnit(), view.name(),
                view.description(), view.basePrice(), view.currency(), view.capacity(), view.active(),
                UnitDetailsMapper.room(view.details()), UnitDetailsMapper.car(view.details()),
                UnitDetailsMapper.transport(view.details()), UnitDetailsMapper.tour(view.details()),
                view.photos().stream().map(PhotoResponse::from).toList());
    }
}
