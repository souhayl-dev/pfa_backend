package com.bookingapp.infrastructure.unit;

import com.bookingapp.domain.unit.UnitType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/** The shared part of every unit. Type-specific columns live in the rooms, cars... tables. */
@Entity
@Table(name = "bookable_units")
public class BookableUnitJpaEntity {

    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(length = 36)
    private UUID id;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "listing_id", nullable = false, length = 36)
    private UUID listingId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private UnitType type;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(name = "base_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;

    @Column(nullable = false)
    private int capacity;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    protected BookableUnitJpaEntity() {
    }

    public BookableUnitJpaEntity(UUID id, UUID listingId, UnitType type, String name, String description,
                                 BigDecimal basePrice, int capacity, boolean active, Instant createdAt,
                                 Instant updatedAt, Instant deletedAt) {
        this.id = id;
        this.listingId = listingId;
        this.type = type;
        this.name = name;
        this.description = description;
        this.basePrice = basePrice;
        this.capacity = capacity;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getListingId() {
        return listingId;
    }

    public UnitType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isActive() {
        return active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }
}
