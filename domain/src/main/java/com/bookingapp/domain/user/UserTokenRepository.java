package com.bookingapp.domain.user;


import java.util.Optional;
import java.util.UUID;

public interface UserTokenRepository {
    UserToken save(UserToken token);

    /** Looks a token up by the hash of the raw value the user sent back. */
    Optional<UserToken> findByHash(String tokenHash, UserTokenType type);

    /** Revokes every still-valid token of this type, for example older reset links once a new one is sent. */
    void revokeAll(UUID userId, UserTokenType type);
}
