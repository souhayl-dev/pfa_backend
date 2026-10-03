package com.bookingapp.infrastructure.booking;

import com.bookingapp.domain.booking.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface BookingJpaRepository extends JpaRepository<BookingJpaEntity, UUID> {
    boolean existsByCode(String code);

    List<BookingJpaEntity> findByClientIdOrderByCreatedAtDesc(UUID clientId);

    /** A booking belongs to the listing of its unit. status null means every status. */
    @Query("""
            select b from BookingJpaEntity b, BookableUnitJpaEntity u
            where b.unitId = u.id and u.listingId = :listingId
              and (:status is null or b.status = :status)
            order by b.createdAt desc
            """)
    List<BookingJpaEntity> findByListing(@Param("listingId") UUID listingId, @Param("status") BookingStatus status);

    /** Pending and confirmed bookings hold their unit; see BookingStatus.holdsAvailability. */
    @Query("""
            select b from BookingJpaEntity b
            where b.unitId = :unitId
              and b.startAt < :endAt and b.endAt > :startAt
              and b.status in (com.bookingapp.domain.booking.BookingStatus.PENDING,
                               com.bookingapp.domain.booking.BookingStatus.CONFIRMED)
            """)
    List<BookingJpaEntity> findHeld(@Param("unitId") UUID unitId, @Param("startAt") Instant startAt,
                                    @Param("endAt") Instant endAt);

    List<BookingJpaEntity> findByStatusAndEndAtBefore(BookingStatus status, Instant instant);
}
