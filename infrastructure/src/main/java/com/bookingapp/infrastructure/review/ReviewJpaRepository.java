package com.bookingapp.infrastructure.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewJpaRepository extends JpaRepository<ReviewJpaEntity, UUID> {
    Optional<ReviewJpaEntity> findByBookingId(UUID bookingId);

    /** A review reaches its listing through its booking and the booked unit. */
    @Query("""
            select r from ReviewJpaEntity r, BookingJpaEntity b, BookableUnitJpaEntity u
            where r.bookingId = b.id and b.unitId = u.id and u.listingId = :listingId
            order by r.createdAt desc
            """)
    List<ReviewJpaEntity> findByListing(@Param("listingId") UUID listingId);
}
