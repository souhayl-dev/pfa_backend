package com.bookingapp.infrastructure.user;

import com.bookingapp.domain.user.UserToken;
import com.bookingapp.domain.user.UserTokenType;
import com.bookingapp.domain.user.UserTokenRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public class UserTokenRepositoryAdapter implements UserTokenRepository {

    private final UserTokenJpaRepository jpaRepository;

    public UserTokenRepositoryAdapter(UserTokenJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public UserToken save(UserToken token) {
        return UserMapper.toDomain(jpaRepository.save(UserMapper.toEntity(token)));
    }

    @Override
    public Optional<UserToken> findByHash(String tokenHash, UserTokenType type) {
        return jpaRepository.findByTokenHashAndType(tokenHash, type).map(UserMapper::toDomain);
    }

    @Override
    @Transactional
    public void revokeAll(UUID userId, UserTokenType type) {
        jpaRepository.revokeAll(userId, type);
    }
}
