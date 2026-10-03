package com.bookingapp.infrastructure.unit;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CarJpaRepository extends JpaRepository<CarJpaEntity, UUID> {
}
