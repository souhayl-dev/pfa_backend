package com.bookingapp.domain.listing;

import com.bookingapp.domain.shared.PlatformCurrency;
import com.bookingapp.domain.shared.Require;
import com.bookingapp.domain.shared.exception.BusinessRuleException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;

/**
 * A place or business a client can see, visit and review: a hotel, a restaurant, a guide, a travel
 * agency or a car rental branch. What the client books inside it are BookableUnits.
 */
public class Listing {

    private final UUID id;
    private final UUID providerId;
    private final ListingType type;
    private String name;
    private String description;
    private Location location;
    private final String currency;
    private String phone;
    private String email;
    private ListingStatus status;
    private ListingDetails details;
    private BigDecimal ratingAvg;
    private int reviewsCount;
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;

    public Listing(UUID id, UUID providerId, String name, String description, Location location, String currency,
                   String phone, String email, ListingStatus status, ListingDetails details, BigDecimal ratingAvg,
                   int reviewsCount, Instant createdAt, Instant updatedAt, Instant deletedAt) {
        this.id = Require.notNull(id, "id");
        this.providerId = Require.notNull(providerId, "providerId");
        this.details = Require.notNull(details, "details");
        this.type = details.type();
        this.name = Require.notBlank(name, "name", 200);
        this.description = Require.optional(description, "description", 4000);
        this.location = Require.notNull(location, "location");
        this.currency = Require.currency(currency);
        this.phone = Require.optional(phone, "phone", 30);
        this.email = optionalEmail(email);
        this.status = Require.notNull(status, "status");
        this.ratingAvg = Require.nonNegative(ratingAvg, "ratingAvg").setScale(2, RoundingMode.HALF_UP);
        this.reviewsCount = reviewsCount;
        this.createdAt = Require.notNull(createdAt, "createdAt");
        this.updatedAt = Require.notNull(updatedAt, "updatedAt");
        this.deletedAt = deletedAt;
    }

    /** A new listing starts as a DRAFT and is only visible to its provider's team. */
    public static Listing draft(UUID id, UUID providerId, String name, String description, Location location,
                                String phone, String email, ListingDetails details, Instant now) {
        return new Listing(id, providerId, name, description, location, PlatformCurrency.CODE, phone, email,
                ListingStatus.DRAFT, details, BigDecimal.ZERO, 0, now, now, null);
    }

    private static String optionalEmail(String email) {
        return email == null || email.isBlank() ? null : Require.email(email, "email");
    }

    public void update(String name, String description, Location location, String phone, String email,
                       Instant now) {
        requireNotDeleted();
        this.name = Require.notBlank(name, "name", 200);
        this.description = Require.optional(description, "description", 4000);
        this.location = Require.notNull(location, "location");
        this.phone = Require.optional(phone, "phone", 30);
        this.email = optionalEmail(email);
        this.updatedAt = now;
    }

    /** The details can change but the type cannot: a hotel stays a hotel. */
    public void updateDetails(ListingDetails newDetails, Instant now) {
        requireNotDeleted();
        Require.notNull(newDetails, "details");
        if (newDetails.type() != type) {
            throw new BusinessRuleException("a " + type + " listing cannot get " + newDetails.type() + " details");
        }
        this.details = newDetails;
        this.updatedAt = now;
    }

    public void activate(Instant now) {
        requireNotDeleted();
        status = ListingStatus.ACTIVE;
        updatedAt = now;
    }

    public void deactivate(Instant now) {
        requireNotDeleted();
        status = ListingStatus.INACTIVE;
        updatedAt = now;
    }

    /** Listings are never hard-deleted because past bookings and reviews point to them. */
    public void softDelete(Instant now) {
        requireNotDeleted();
        status = ListingStatus.INACTIVE;
        deletedAt = now;
        updatedAt = now;
    }

    /** Keeps the cached rating in step with the reviews table, in the same transaction as the review change. */
    public void applyReviewChange(Integer removedRating, Integer addedRating) {
        BigDecimal total = ratingAvg.multiply(BigDecimal.valueOf(reviewsCount));
        if (removedRating != null) {
            total = total.subtract(BigDecimal.valueOf(removedRating));
            reviewsCount--;
        }
        if (addedRating != null) {
            total = total.add(BigDecimal.valueOf(addedRating));
            reviewsCount++;
        }
        if (reviewsCount < 0) {
            throw new IllegalStateException("reviewsCount cannot go below zero");
        }
        ratingAvg = reviewsCount == 0
                ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
                : total.divide(BigDecimal.valueOf(reviewsCount), 2, RoundingMode.HALF_UP);
    }

    public boolean isBookable() {
        return status == ListingStatus.ACTIVE && deletedAt == null;
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    private void requireNotDeleted() {
        if (deletedAt != null) {
            throw new BusinessRuleException("this listing has been deleted");
        }
    }

    public UUID id() {
        return id;
    }

    public UUID providerId() {
        return providerId;
    }

    public ListingType type() {
        return type;
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public Location location() {
        return location;
    }

    public String currency() {
        return currency;
    }

    public String phone() {
        return phone;
    }

    public String email() {
        return email;
    }

    public ListingStatus status() {
        return status;
    }

    public ListingDetails details() {
        return details;
    }

    public BigDecimal ratingAvg() {
        return ratingAvg;
    }

    public int reviewsCount() {
        return reviewsCount;
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
