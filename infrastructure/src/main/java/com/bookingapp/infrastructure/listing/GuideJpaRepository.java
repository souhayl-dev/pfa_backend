package com.bookingapp.infrastructure.listing;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface GuideJpaRepository extends JpaRepository<GuideJpaEntity, UUID> {
}
