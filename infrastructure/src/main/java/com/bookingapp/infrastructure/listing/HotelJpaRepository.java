package com.bookingapp.infrastructure.listing;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HotelJpaRepository extends JpaRepository<HotelJpaEntity, UUID> {
}
