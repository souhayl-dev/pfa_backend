package com.bookingapp.infrastructure.listing;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ListingJpaRepository extends JpaRepository<ListingJpaEntity, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select l from ListingJpaEntity l where l.id = :id")
    Optional<ListingJpaEntity> findByIdForUpdate(@Param("id") UUID id);

    List<ListingJpaEntity> findByProviderIdAndDeletedAtIsNullOrderByCreatedAtAsc(UUID providerId);
}
