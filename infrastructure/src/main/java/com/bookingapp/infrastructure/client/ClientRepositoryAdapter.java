package com.bookingapp.infrastructure.client;

import com.bookingapp.domain.client.Client;
import com.bookingapp.domain.client.ClientRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class ClientRepositoryAdapter implements ClientRepository {

    private final ClientJpaRepository jpaRepository;

    public ClientRepositoryAdapter(ClientJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Client save(Client client) {
        return ClientMapper.toDomain(jpaRepository.save(ClientMapper.toEntity(client)));
    }

    @Override
    public Optional<Client> findById(UUID id) {
        return jpaRepository.findById(id).map(ClientMapper::toDomain);
    }

    @Override
    public Optional<Client> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).map(ClientMapper::toDomain);
    }
}
