package com.bookingapp.infrastructure.booking;

import com.bookingapp.domain.booking.Booking;
import com.bookingapp.domain.booking.BookingStatusChange;
import com.bookingapp.domain.shared.Money;

public final class BookingMapper {

    private BookingMapper() {
    }

    public static Booking toDomain(BookingJpaEntity e) {
        return new Booking(e.getId(), e.getCode(), e.getClientId(), e.getUnitId(), e.getStatus(), e.getStartAt(),
                e.getEndAt(), e.getGuestsCount(), e.getUnitPrice(), Money.of(e.getTotalAmount(), e.getCurrency()),
                e.getSpecialRequests(), e.getCreatedAt(), e.getUpdatedAt());
    }

    public static BookingJpaEntity toEntity(Booking b) {
        return new BookingJpaEntity(b.id(), b.code(), b.clientId(), b.unitId(), b.status(), b.startAt(), b.endAt(),
                b.guestsCount(), b.unitPrice(), b.total().amount(), b.total().currency(), b.specialRequests(),
                b.createdAt(), b.updatedAt());
    }

    public static BookingStatusChange toDomain(BookingStatusHistoryJpaEntity e) {
        return new BookingStatusChange(e.getId(), e.getBookingId(), e.getFromStatus(), e.getToStatus(),
                e.getChangedBy(), e.getReason(), e.getChangedAt());
    }

    public static BookingStatusHistoryJpaEntity toEntity(BookingStatusChange c) {
        return new BookingStatusHistoryJpaEntity(c.id(), c.bookingId(), c.fromStatus(), c.toStatus(), c.changedBy(),
                c.reason(), c.changedAt());
    }
}
