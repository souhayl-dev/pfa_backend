package com.bookingapp.infrastructure.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BookingStatusHistoryJpaRepository extends JpaRepository<BookingStatusHistoryJpaEntity, UUID> {
    List<BookingStatusHistoryJpaEntity> findByBookingIdOrderByChangedAtAsc(UUID bookingId);
}
