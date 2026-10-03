package com.bookingapp.api.booking;

import com.bookingapp.application.booking.BookingView;
import com.bookingapp.domain.booking.BookingStatus;
import com.bookingapp.domain.unit.UnitType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * startAt and endAt are UTC; timezone is the listing's, for display. clientName is only filled for
 * the provider's team. reviewId is null until the booking is reviewed.
 */
public record BookingResponse(UUID id, String code, UUID listingId, String listingName, String timezone,
                              UUID unitId, String unitName, UnitType unitType, BookingStatus status,
                              Instant startAt, Instant endAt, int guestsCount, BigDecimal unitPrice,
                              BigDecimal totalAmount, String currency, String specialRequests, String clientName,
                              UUID reviewId, Instant createdAt, Instant updatedAt) {

    public static BookingResponse from(BookingView view) {
        return new BookingResponse(view.id(), view.code(), view.listingId(), view.listingName(), view.timezone(),
                view.unitId(), view.unitName(), view.unitType(), view.status(), view.startAt(), view.endAt(),
                view.guestsCount(), view.unitPrice(), view.totalAmount(), view.currency(), view.specialRequests(),
                view.clientName(), view.reviewId(), view.createdAt(), view.updatedAt());
    }
}
