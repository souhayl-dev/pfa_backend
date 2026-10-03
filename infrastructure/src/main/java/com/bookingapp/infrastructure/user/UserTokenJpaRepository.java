package com.bookingapp.infrastructure.user;

import com.bookingapp.domain.user.UserTokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserTokenJpaRepository extends JpaRepository<UserTokenJpaEntity, UUID> {
    Optional<UserTokenJpaEntity> findByTokenHashAndType(String tokenHash, UserTokenType type);

    @Modifying
    @Query("update UserTokenJpaEntity t set t.revoked = true "
            + "where t.userId = :userId and t.type = :type and t.revoked = false")
    void revokeAll(@Param("userId") UUID userId, @Param("type") UserTokenType type);
}
