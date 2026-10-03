package com.bookingapp.infrastructure.provider;

import com.bookingapp.domain.provider.ProviderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProviderJpaRepository extends JpaRepository<ProviderJpaEntity, UUID> {
    List<ProviderJpaEntity> findByStatusOrderByCreatedAtAsc(ProviderStatus status);
}
