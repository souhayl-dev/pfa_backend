package com.bookingapp.infrastructure.team;

import com.bookingapp.domain.team.ProviderMember;

public final class ProviderMemberMapper {

    private ProviderMemberMapper() {
    }

    public static ProviderMember toDomain(ProviderMemberJpaEntity e) {
        return new ProviderMember(e.getId(), e.getProviderId(), e.getUserId(), e.getRole(), e.getStatus(),
                e.getJoinedAt());
    }

    public static ProviderMemberJpaEntity toEntity(ProviderMember m) {
        return new ProviderMemberJpaEntity(m.id(), m.providerId(), m.userId(), m.role(), m.status(), m.joinedAt());
    }
}
