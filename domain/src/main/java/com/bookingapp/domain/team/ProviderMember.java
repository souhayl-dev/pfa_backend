package com.bookingapp.domain.team;

import com.bookingapp.domain.shared.Require;
import com.bookingapp.domain.shared.exception.BusinessRuleException;
import com.bookingapp.domain.shared.exception.UnauthorizedActionException;

import java.time.Instant;
import java.util.UUID;

/** A user working for a provider. Every active member sees all of the provider's listings. */
public class ProviderMember {

    private final UUID id;
    private final UUID providerId;
    private final UUID userId;
    private MemberRole role;
    private MemberStatus status;
    private final Instant joinedAt;

    public ProviderMember(UUID id, UUID providerId, UUID userId, MemberRole role, MemberStatus status,
                          Instant joinedAt) {
        this.id = Require.notNull(id, "id");
        this.providerId = Require.notNull(providerId, "providerId");
        this.userId = Require.notNull(userId, "userId");
        this.role = Require.notNull(role, "role");
        this.status = Require.notNull(status, "status");
        this.joinedAt = Require.notNull(joinedAt, "joinedAt");
    }

    public static ProviderMember owner(UUID id, UUID providerId, UUID userId, Instant now) {
        return new ProviderMember(id, providerId, userId, MemberRole.OWNER, MemberStatus.ACTIVE, now);
    }

    public static ProviderMember join(UUID id, UUID providerId, UUID userId, MemberRole role, Instant now) {
        return new ProviderMember(id, providerId, userId, role, MemberStatus.ACTIVE, now);
    }

    public boolean isActive() {
        return status == MemberStatus.ACTIVE;
    }

    public boolean isOwner() {
        return role == MemberRole.OWNER;
    }

    public void requireCanManageListings() {
        if (!isActive() || !role.canManageListings()) {
            throw new UnauthorizedActionException("only owners and managers can manage listings");
        }
    }

    public void requireCanManageProvider() {
        if (!isActive() || !role.canManageProvider()) {
            throw new UnauthorizedActionException("only owners can change the company details");
        }
    }

    /** The acting member must be allowed to manage a member with the given role. */
    public void requireCanManage(MemberRole targetRole) {
        if (!isActive() || !role.canManageTeam() || !role.canAssign(targetRole)) {
            throw new UnauthorizedActionException("you cannot manage team members with the role " + targetRole);
        }
    }

    /** Callers must make sure the provider keeps at least one active owner. */
    public void changeRole(MemberRole newRole) {
        this.role = Require.notNull(newRole, "role");
    }

    public void suspend() {
        if (status == MemberStatus.SUSPENDED) {
            throw new BusinessRuleException("this member is already suspended");
        }
        status = MemberStatus.SUSPENDED;
    }

    public void reactivate() {
        status = MemberStatus.ACTIVE;
    }

    public UUID id() {
        return id;
    }

    public UUID providerId() {
        return providerId;
    }

    public UUID userId() {
        return userId;
    }

    public MemberRole role() {
        return role;
    }

    public MemberStatus status() {
        return status;
    }

    public Instant joinedAt() {
        return joinedAt;
    }
}
