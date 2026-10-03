package com.bookingapp.domain.unit;

import com.bookingapp.domain.listing.Listing;
import com.bookingapp.domain.shared.Require;
import com.bookingapp.domain.shared.exception.BusinessRuleException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;

/**
 * Something a client books inside a listing: a room, a car, a table, a guide day, a transfer or a
 * tour. capacity is the number of people it takes: guests in a room, seats in a car, group size
 * of a tour.
 */
public class BookableUnit {

    private final UUID id;
    private final UUID listingId;
    private final UnitType type;
    private String name;
    private String description;
    private BigDecimal basePrice;
    private int capacity;
    private UnitDetails details;
    private boolean active;
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    public BookableUnit(UUID id, UUID listingId, UnitType type, String name, String description,
                        BigDecimal basePrice, int capacity, UnitDetails details, boolean active, Instant createdAt,
                        Instant updatedAt, Instant deletedAt) {
        this.id = Require.notNull(id, "id");
        this.listingId = Require.notNull(listingId, "listingId");
        this.type = Require.notNull(type, "type");
        this.name = Require.notBlank(name, "name", 200);
        this.description = Require.optional(description, "description", 2000);
        this.basePrice = price(basePrice);
        this.capacity = Require.positive(capacity, "capacity");
        this.details = checkedDetails(type, details);
        this.active = active;
        this.createdAt = Require.notNull(createdAt, "createdAt");
        this.updatedAt = Require.notNull(updatedAt, "updatedAt");
        this.deletedAt = deletedAt;
    }

    /** Each kind of listing only offers its own unit types: rooms in hotels, cars in car agencies. */
    public static BookableUnit create(UUID id, Listing listing, UnitType type, String name, String description,
                                      BigDecimal basePrice, int capacity, UnitDetails details, Instant now) {
        if (listing.isDeleted()) {
            throw new BusinessRuleException("cannot add units to a deleted listing");
        }
        Require.notNull(type, "type");
        if (type.listingType() != listing.type()) {
            throw new BusinessRuleException("a " + listing.type() + " listing cannot offer " + type + " units");
        }
        return new BookableUnit(id, listing.id(), type, name, description, basePrice, capacity, details, true, now,
                now, null);
    }

    private static UnitDetails checkedDetails(UnitType type, UnitDetails details) {
        if (!type.hasDetails()) {
            if (details != null) {
                throw new IllegalArgumentException(type + " units take no details");
            }
            return null;
        }
        Require.notNull(details, "details");
        if (details.type() != type) {
            throw new IllegalArgumentException(type + " units need " + type + " details, not " + details.type());
        }
        return details;
    }

    private static BigDecimal price(BigDecimal value) {
        return Require.nonNegative(value, "basePrice").setScale(2, RoundingMode.HALF_UP);
    }

    public void update(String name, String description, BigDecimal basePrice, int capacity, UnitDetails details,
                       Instant now) {
        requireNotDeleted();
        this.name = Require.notBlank(name, "name", 200);
        this.description = Require.optional(description, "description", 2000);
        this.basePrice = price(basePrice);
        this.capacity = Require.positive(capacity, "capacity");
        this.details = checkedDetails(type, details);
        this.updatedAt = now;
    }

    public void activate(Instant now) {
        requireNotDeleted();
        active = true;
        updatedAt = now;
    }

    public void deactivate(Instant now) {
        requireNotDeleted();
        active = false;
        updatedAt = now;
    }

    /** Units are never hard-deleted because past bookings point to them. */
    public void softDelete(Instant now) {
        requireNotDeleted();
        active = false;
        deletedAt = now;
        updatedAt = now;
    }

    public boolean isBookable() {
        return active && deletedAt == null;
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    private void requireNotDeleted() {
        if (deletedAt != null) {
            throw new BusinessRuleException("this unit has been deleted");
        }
    }

    public UUID id() {
        return id;
    }

    public UUID listingId() {
        return listingId;
    }

    public UnitType type() {
        return type;
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public BigDecimal basePrice() {
        return basePrice;
    }

    public int capacity() {
        return capacity;
    }

    public UnitDetails details() {
        return details;
    }

    public boolean active() {
        return active;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public Instant deletedAt() {
        return deletedAt;
    }
}
