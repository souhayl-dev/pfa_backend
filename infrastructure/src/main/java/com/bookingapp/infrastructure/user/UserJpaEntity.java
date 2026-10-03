package com.bookingapp.infrastructure.user;

import com.bookingapp.domain.user.Gender;
import com.bookingapp.domain.user.UserRole;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
public class UserJpaEntity {

    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(length = 36)
    private UUID id;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false)
    private String email;

    @Column(length = 50)
    private String username;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(length = 30)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Gender gender;

    @Column(name = "profile_image", length = 500)
    private String profileImage;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "is_verified", nullable = false)
    private boolean verified;

    @Column(name = "preferred_currency", nullable = false, length = 3)
    private String preferredCurrency;

    @Column(name = "notifications_enabled", nullable = false)
    private boolean notificationsEnabled;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 20)
    private Set<UserRole> roles = new HashSet<>();

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected UserJpaEntity() {
    }

    public UserJpaEntity(UUID id, String firstName, String lastName, String email, String username,
                         String passwordHash, String phone, Gender gender, String profileImage, boolean active,
                         boolean verified, String preferredCurrency, boolean notificationsEnabled,
                         Set<UserRole> roles, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.passwordHash = passwordHash;
        this.phone = phone;
        this.gender = gender;
        this.profileImage = profileImage;
        this.active = active;
        this.verified = verified;
        this.preferredCurrency = preferredCurrency;
        this.notificationsEnabled = notificationsEnabled;
        this.roles = new HashSet<>(roles);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getPhone() {
        return phone;
    }

    public Gender getGender() {
        return gender;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isVerified() {
        return verified;
    }

    public String getPreferredCurrency() {
        return preferredCurrency;
    }

    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

    public Set<UserRole> getRoles() {
        return roles;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
