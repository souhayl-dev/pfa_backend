package com.bookingapp.domain.provider;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProviderRepository {
    Provider save(Provider provider);

    Optional<Provider> findById(UUID id);

    List<Provider> findByStatus(ProviderStatus status);
}
