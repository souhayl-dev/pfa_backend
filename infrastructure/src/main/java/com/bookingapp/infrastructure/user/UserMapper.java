package com.bookingapp.infrastructure.user;

import com.bookingapp.domain.user.User;
import com.bookingapp.domain.user.UserToken;

public final class UserMapper {

    private UserMapper() {
    }

    public static User toDomain(UserJpaEntity e) {
        return new User(e.getId(), e.getFirstName(), e.getLastName(), e.getEmail(), e.getUsername(),
                e.getPasswordHash(), e.getPhone(), e.getGender(), e.getProfileImage(), e.isActive(), e.isVerified(),
                e.isNotificationsEnabled(), e.getRoles(), e.getCreatedAt(),
                e.getUpdatedAt());
    }

    public static UserJpaEntity toEntity(User u) {
        return new UserJpaEntity(u.id(), u.firstName(), u.lastName(), u.email(), u.username(), u.passwordHash(),
                u.phone(), u.gender(), u.profileImage(), u.active(), u.verified(),
                u.notificationsEnabled(), u.roles(), u.createdAt(), u.updatedAt());
    }

    public static UserToken toDomain(UserTokenJpaEntity e) {
        return new UserToken(e.getId(), e.getUserId(), e.getTokenHash(), e.getType(), e.getExpiresAt(),
                e.isRevoked(), e.getCreatedAt());
    }

    public static UserTokenJpaEntity toEntity(UserToken t) {
        return new UserTokenJpaEntity(t.id(), t.userId(), t.tokenHash(), t.type(), t.expiresAt(), t.revoked(),
                t.createdAt());
    }
}
