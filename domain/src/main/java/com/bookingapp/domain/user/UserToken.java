package com.bookingapp.domain.user;

import com.bookingapp.domain.shared.exception.BusinessRuleException;
import com.bookingapp.domain.shared.Require;

import java.time.Instant;
import java.util.UUID;

public class UserToken {

    private final UUID id;
    private final UUID userId;
    private final String tokenHash;
    private final UserTokenType type;
    private final Instant expiresAt;
    private boolean revoked;
    private final Instant createdAt;

    public UserToken(UUID id, UUID userId, String tokenHash, UserTokenType type, Instant expiresAt, boolean revoked,
                     Instant createdAt) {
        this.id = Require.notNull(id, "id");
        this.userId = Require.notNull(userId, "userId");
        this.tokenHash = Require.notBlank(tokenHash, "tokenHash", 64);
        this.type = Require.notNull(type, "type");
        this.expiresAt = Require.notNull(expiresAt, "expiresAt");
        this.revoked = revoked;
        this.createdAt = Require.notNull(createdAt, "createdAt");
    }

    /** Creates a token from the raw value that is sent to the user; only its hash is kept. */
    public static UserToken issue(UUID id, UUID userId, String rawToken, UserTokenType type, Instant now) {
        return new UserToken(id, userId, TokenHash.of(rawToken), type, now.plus(type.lifetime()), false, now);
    }

    public boolean isUsable(Instant now) {
        return !revoked && now.isBefore(expiresAt);
    }

    /** Marks a single-use token as used. Fails if it was already used, revoked or has expired. */
    public void consume(Instant now) {
        if (!isUsable(now)) {
            throw new BusinessRuleException("this link has expired or was already used");
        }
        revoked = true;
    }

    public void revoke() {
        revoked = true;
    }

    public UUID id() {
        return id;
    }

    public UUID userId() {
        return userId;
    }

    public String tokenHash() {
        return tokenHash;
    }

    public UserTokenType type() {
        return type;
    }

    public Instant expiresAt() {
        return expiresAt;
    }

    public boolean revoked() {
        return revoked;
    }

    public Instant createdAt() {
        return createdAt;
    }
}
