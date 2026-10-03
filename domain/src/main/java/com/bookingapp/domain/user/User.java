package com.bookingapp.domain.user;

import com.bookingapp.domain.shared.exception.BusinessRuleException;
import com.bookingapp.domain.shared.Require;

import java.time.Instant;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

/** One account per person. What a user can do comes from their Client profile and their ProviderMember rows. */
public class User {

    private static final Pattern USERNAME = Pattern.compile("[a-z0-9._]{3,50}");

    private final UUID id;
    private String firstName;
    private String lastName;
    private final String email;
    private String username;
    private String passwordHash;
    private String phone;
    private Gender gender;
    private String profileImage;
    private boolean active;
    private boolean verified;
    private String preferredCurrency;
    private boolean notificationsEnabled;
    private final Set<UserRole> roles;
    private final Instant createdAt;
    private Instant updatedAt;

    public User(UUID id, String firstName, String lastName, String email, String username, String passwordHash,
                String phone, Gender gender, String profileImage, boolean active, boolean verified,
                String preferredCurrency, boolean notificationsEnabled, Set<UserRole> roles, Instant createdAt,
                Instant updatedAt) {
        this.id = Require.notNull(id, "id");
        this.firstName = Require.notBlank(firstName, "firstName", 100);
        this.lastName = Require.notBlank(lastName, "lastName", 100);
        this.email = Require.email(email, "email");
        this.username = validUsername(username);
        this.passwordHash = Require.optional(passwordHash, "passwordHash", 255);
        this.phone = Require.optional(phone, "phone", 30);
        this.gender = gender;
        this.profileImage = Require.optional(profileImage, "profileImage", 500);
        this.active = active;
        this.verified = verified;
        this.preferredCurrency = Require.currency(preferredCurrency);
        this.notificationsEnabled = notificationsEnabled;
        this.roles = roles.isEmpty() ? EnumSet.noneOf(UserRole.class) : EnumSet.copyOf(roles);
        this.createdAt = Require.notNull(createdAt, "createdAt");
        this.updatedAt = Require.notNull(updatedAt, "updatedAt");
    }

    public static User register(UUID id, String firstName, String lastName, String email, String passwordHash,
                                Instant now) {
        Require.notBlank(passwordHash, "passwordHash", 255);
        return new User(id, firstName, lastName, email, null, passwordHash, null, null, null, true, false, "EUR",
                true, Set.of(), now, now);
    }

    private static String validUsername(String username) {
        if (username == null || username.isBlank()) {
            return null;
        }
        String normalized = username.strip().toLowerCase();
        if (!USERNAME.matcher(normalized).matches()) {
            throw new IllegalArgumentException(
                    "username must be 3 to 50 characters: lowercase letters, digits, dots or underscores");
        }
        return normalized;
    }

    public void updateProfile(String firstName, String lastName, String username, String phone, Gender gender,
                              String preferredCurrency, boolean notificationsEnabled, Instant now) {
        this.firstName = Require.notBlank(firstName, "firstName", 100);
        this.lastName = Require.notBlank(lastName, "lastName", 100);
        this.username = validUsername(username);
        this.phone = Require.optional(phone, "phone", 30);
        this.gender = gender;
        this.preferredCurrency = Require.currency(preferredCurrency);
        this.notificationsEnabled = notificationsEnabled;
        this.updatedAt = now;
    }

    public void changeProfileImage(String url, Instant now) {
        this.profileImage = Require.optional(url, "profileImage", 500);
        this.updatedAt = now;
    }

    public void changePassword(String newPasswordHash, Instant now) {
        this.passwordHash = Require.notBlank(newPasswordHash, "passwordHash", 255);
        this.updatedAt = now;
    }

    public void markVerified(Instant now) {
        this.verified = true;
        this.updatedAt = now;
    }

    public void deactivate(Instant now) {
        this.active = false;
        this.updatedAt = now;
    }

    public void activate(Instant now) {
        this.active = true;
        this.updatedAt = now;
    }

    /** Only active accounts may sign in or act. */
    public void requireActive() {
        if (!active) {
            throw new BusinessRuleException("this account is deactivated");
        }
    }

    public boolean hasPassword() {
        return passwordHash != null;
    }

    public boolean isAdmin() {
        return roles.contains(UserRole.ADMIN);
    }

    public String fullName() {
        return firstName + " " + lastName;
    }

    public UUID id() {
        return id;
    }

    public String firstName() {
        return firstName;
    }

    public String lastName() {
        return lastName;
    }

    public String email() {
        return email;
    }

    public String username() {
        return username;
    }

    public String passwordHash() {
        return passwordHash;
    }

    public String phone() {
        return phone;
    }

    public Gender gender() {
        return gender;
    }

    public String profileImage() {
        return profileImage;
    }

    public boolean active() {
        return active;
    }

    public boolean verified() {
        return verified;
    }

    public String preferredCurrency() {
        return preferredCurrency;
    }

    public boolean notificationsEnabled() {
        return notificationsEnabled;
    }

    public Set<UserRole> roles() {
        return Set.copyOf(roles);
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
