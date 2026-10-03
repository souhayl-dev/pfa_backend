package com.bookingapp.infrastructure.team;

import com.bookingapp.domain.team.MemberRole;
import com.bookingapp.domain.team.MemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProviderMemberJpaRepository extends JpaRepository<ProviderMemberJpaEntity, UUID> {
    Optional<ProviderMemberJpaEntity> findByProviderIdAndUserId(UUID providerId, UUID userId);

    List<ProviderMemberJpaEntity> findByProviderIdOrderByJoinedAtAsc(UUID providerId);

    List<ProviderMemberJpaEntity> findByUserIdOrderByJoinedAtAsc(UUID userId);

    long countByProviderIdAndRoleAndStatus(UUID providerId, MemberRole role, MemberStatus status);
}
