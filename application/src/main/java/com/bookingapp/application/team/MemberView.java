package com.bookingapp.application.team;

import com.bookingapp.domain.team.MemberRole;
import com.bookingapp.domain.team.MemberStatus;
import com.bookingapp.domain.team.ProviderMember;
import com.bookingapp.domain.user.User;

import java.time.Instant;
import java.util.UUID;

public record MemberView(UUID id, UUID userId, String firstName, String lastName, String email, MemberRole role,
                         MemberStatus status, Instant joinedAt) {

    public static MemberView from(ProviderMember member, User user) {
        return new MemberView(member.id(), user.id(), user.firstName(), user.lastName(), user.email(),
                member.role(), member.status(), member.joinedAt());
    }
}
