package com.bookingapp.domain.team;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProviderMemberRepository {
    ProviderMember save(ProviderMember member);

    Optional<ProviderMember> findById(UUID id);

    Optional<ProviderMember> findByProviderAndUser(UUID providerId, UUID userId);

    List<ProviderMember> findByProvider(UUID providerId);

    /** Every provider a user works for, for the "switch account" menu. */
    List<ProviderMember> findByUser(UUID userId);

    long countActiveOwners(UUID providerId);

    void delete(UUID memberId);
}
