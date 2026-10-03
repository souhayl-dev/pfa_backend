package com.bookingapp.infrastructure.provider;

import com.bookingapp.domain.provider.Provider;
import com.bookingapp.domain.provider.ProviderStatus;
import com.bookingapp.domain.provider.ProviderRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ProviderRepositoryAdapter implements ProviderRepository {

    private final ProviderJpaRepository jpaRepository;

    public ProviderRepositoryAdapter(ProviderJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Provider save(Provider provider) {
        return ProviderMapper.toDomain(jpaRepository.save(ProviderMapper.toEntity(provider)));
    }

    @Override
    public Optional<Provider> findById(UUID id) {
        return jpaRepository.findById(id).map(ProviderMapper::toDomain);
    }

    @Override
    public List<Provider> findByStatus(ProviderStatus status) {
        return jpaRepository.findByStatusOrderByCreatedAtAsc(status).stream()
                .map(ProviderMapper::toDomain)
                .toList();
    }
}
