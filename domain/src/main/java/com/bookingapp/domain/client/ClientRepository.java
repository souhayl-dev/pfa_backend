package com.bookingapp.domain.client;


import java.util.Optional;
import java.util.UUID;

public interface ClientRepository {
    Client save(Client client);

    Optional<Client> findById(UUID id);

    Optional<Client> findByUserId(UUID userId);
}
