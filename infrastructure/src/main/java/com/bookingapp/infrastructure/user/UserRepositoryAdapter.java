package com.bookingapp.infrastructure.user;

import com.bookingapp.domain.user.User;
import com.bookingapp.domain.user.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository jpaRepository;

    public UserRepositoryAdapter(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public User save(User user) {
        return UserMapper.toDomain(jpaRepository.save(UserMapper.toEntity(user)));
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaRepository.findById(id).map(UserMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email.strip().toLowerCase()).map(UserMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email.strip().toLowerCase());
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaRepository.existsByUsername(username.strip().toLowerCase());
    }

    @Override
    public List<User> findAll() {
        return jpaRepository.findAll().stream().map(UserMapper::toDomain).toList();
    }
}
