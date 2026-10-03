package com.bookingapp.infrastructure.unit;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransportJpaRepository extends JpaRepository<TransportJpaEntity, UUID> {
}
