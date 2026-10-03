package com.bookingapp.api.team;

import com.bookingapp.application.team.MemberView;
import com.bookingapp.domain.team.MemberRole;
import com.bookingapp.domain.team.MemberStatus;

import java.time.Instant;
import java.util.UUID;

public record MemberResponse(UUID id, UUID userId, String firstName, String lastName, String email,
                             MemberRole role, MemberStatus status, Instant joinedAt) {

    public static MemberResponse from(MemberView view) {
        return new MemberResponse(view.id(), view.userId(), view.firstName(), view.lastName(), view.email(),
                view.role(), view.status(), view.joinedAt());
    }
}
