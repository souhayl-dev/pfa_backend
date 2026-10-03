package com.bookingapp.application.unit;

import com.bookingapp.application.photo.PhotoView;
import com.bookingapp.domain.photo.Photo;
import com.bookingapp.domain.unit.BookableUnit;
import com.bookingapp.domain.unit.UnitDetails;
import com.bookingapp.domain.unit.UnitType;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/** currency is the listing's currency; pricingUnit says what basePrice is charged per. */
public record UnitView(UUID id, UUID listingId, UnitType type, UnitType.PricingUnit pricingUnit, String name,
                       String description, BigDecimal basePrice, String currency, int capacity, boolean active,
                       UnitDetails details, List<PhotoView> photos) {

    public static UnitView from(BookableUnit unit, String currency, List<Photo> photos) {
        return new UnitView(unit.id(), unit.listingId(), unit.type(), unit.type().pricingUnit(), unit.name(),
                unit.description(), unit.basePrice(), currency, unit.capacity(), unit.active(), unit.details(),
                photos.stream().map(PhotoView::from).toList());
    }
}
