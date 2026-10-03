package com.bookingapp.infrastructure.client;

import com.bookingapp.domain.client.Client;

public final class ClientMapper {

    private ClientMapper() {
    }

    public static Client toDomain(ClientJpaEntity e) {
        return new Client(e.getId(), e.getUserId(), e.getNationality(), e.getBirthDate(), e.getCreatedAt(),
                e.getUpdatedAt());
    }

    public static ClientJpaEntity toEntity(Client c) {
        return new ClientJpaEntity(c.id(), c.userId(), c.nationality(), c.birthDate(), c.createdAt(), c.updatedAt());
    }
}
