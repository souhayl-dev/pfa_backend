package com.bookingapp.infrastructure.photo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PhotoJpaRepository extends JpaRepository<PhotoJpaEntity, UUID> {
    List<PhotoJpaEntity> findByListingIdOrderBySortOrderAsc(UUID listingId);

    List<PhotoJpaEntity> findByUnitIdOrderBySortOrderAsc(UUID unitId);
}
