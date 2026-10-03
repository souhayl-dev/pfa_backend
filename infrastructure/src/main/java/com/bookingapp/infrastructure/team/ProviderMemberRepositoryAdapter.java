package com.bookingapp.infrastructure.team;

import com.bookingapp.domain.team.MemberRole;
import com.bookingapp.domain.team.MemberStatus;
import com.bookingapp.domain.team.ProviderMember;
import com.bookingapp.domain.team.ProviderMemberRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ProviderMemberRepositoryAdapter implements ProviderMemberRepository {

    private final ProviderMemberJpaRepository jpaRepository;

    public ProviderMemberRepositoryAdapter(ProviderMemberJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ProviderMember save(ProviderMember member) {
        return ProviderMemberMapper.toDomain(jpaRepository.save(ProviderMemberMapper.toEntity(member)));
    }

    @Override
    public Optional<ProviderMember> findById(UUID id) {
        return jpaRepository.findById(id).map(ProviderMemberMapper::toDomain);
    }

    @Override
    public Optional<ProviderMember> findByProviderAndUser(UUID providerId, UUID userId) {
        return jpaRepository.findByProviderIdAndUserId(providerId, userId).map(ProviderMemberMapper::toDomain);
    }

    @Override
    public List<ProviderMember> findByProvider(UUID providerId) {
        return jpaRepository.findByProviderIdOrderByJoinedAtAsc(providerId).stream()
                .map(ProviderMemberMapper::toDomain)
                .toList();
    }

    @Override
    public List<ProviderMember> findByUser(UUID userId) {
        return jpaRepository.findByUserIdOrderByJoinedAtAsc(userId).stream()
                .map(ProviderMemberMapper::toDomain)
                .toList();
    }

    @Override
    public long countActiveOwners(UUID providerId) {
        return jpaRepository.countByProviderIdAndRoleAndStatus(providerId, MemberRole.OWNER, MemberStatus.ACTIVE);
    }

    @Override
    public void delete(UUID memberId) {
        jpaRepository.deleteById(memberId);
    }
}
