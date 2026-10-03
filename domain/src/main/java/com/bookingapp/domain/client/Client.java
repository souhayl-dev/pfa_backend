package com.bookingapp.domain.client;

import com.bookingapp.domain.shared.Require;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/** The booking side of a user. Created at the user's first booking. */
public class Client {

    private final UUID id;
    private final UUID userId;
    private String nationality;
    private LocalDate birthDate;
    private final Instant createdAt;
    private Instant updatedAt;

    public Client(UUID id, UUID userId, String nationality, LocalDate birthDate, Instant createdAt,
                  Instant updatedAt) {
        this.id = Require.notNull(id, "id");
        this.userId = Require.notNull(userId, "userId");
        this.nationality = nationality == null ? null : Require.countryCode(nationality, "nationality");
        this.birthDate = birthDate;
        this.createdAt = Require.notNull(createdAt, "createdAt");
        this.updatedAt = Require.notNull(updatedAt, "updatedAt");
    }

    public static Client create(UUID id, UUID userId, Instant now) {
        return new Client(id, userId, null, null, now, now);
    }

    public void updateDetails(String nationality, LocalDate birthDate, LocalDate today, Instant now) {
        if (birthDate != null && !birthDate.isBefore(today)) {
            throw new IllegalArgumentException("birthDate must be in the past");
        }
        this.nationality = nationality == null ? null : Require.countryCode(nationality, "nationality");
        this.birthDate = birthDate;
        this.updatedAt = now;
    }

    public UUID id() {
        return id;
    }

    public UUID userId() {
        return userId;
    }

    public String nationality() {
        return nationality;
    }

    public LocalDate birthDate() {
        return birthDate;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
