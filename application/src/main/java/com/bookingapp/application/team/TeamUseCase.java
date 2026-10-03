package com.bookingapp.application.team;

import com.bookingapp.domain.team.MemberRole;

import java.util.List;
import java.util.UUID;

/** A provider's team. Owners manage everyone; managers can add and remove staff. */
public interface TeamUseCase {
    List<MemberView> members(UUID userId, UUID providerId);

    /** Adds a person who already has an account, found by their email. */
    MemberView addMember(UUID userId, UUID providerId, String email, MemberRole role);

    MemberView changeRole(UUID userId, UUID memberId, MemberRole role);

    MemberView suspend(UUID userId, UUID memberId);

    MemberView reactivate(UUID userId, UUID memberId);

    void remove(UUID userId, UUID memberId);
}
