package com.bookingapp.infrastructure.listing;

import com.bookingapp.domain.listing.ListingStatus;
import com.bookingapp.domain.listing.ListingType;
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

/** The shared part of every listing. Type-specific columns live in the hotels, restaurants... tables. */
@Entity
@Table(name = "listings")
public class ListingJpaEntity {

    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(length = 36)
    private UUID id;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "provider_id", nullable = false, length = 36)
    private UUID providerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ListingType type;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 4000)
    private String description;

    private String address;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(name = "country_code", nullable = false, length = 2)
    private String countryCode;

    @Column(precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(precision = 9, scale = 6)
    private BigDecimal longitude;

    @Column(nullable = false, length = 64)
    private String timezone;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(length = 30)
    private String phone;

    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ListingStatus status;

    @Column(name = "rating_avg", nullable = false, precision = 3, scale = 2)
    private BigDecimal ratingAvg;

    @Column(name = "reviews_count", nullable = false)
    private int reviewsCount;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    protected ListingJpaEntity() {
    }

    public ListingJpaEntity(UUID id, UUID providerId, ListingType type, String name, String description,
                            String address, String city, String countryCode, BigDecimal latitude,
                            BigDecimal longitude, String timezone, String currency, String phone, String email,
                            ListingStatus status, BigDecimal ratingAvg, int reviewsCount, Instant createdAt,
                            Instant updatedAt, Instant deletedAt) {
        this.id = id;
        this.providerId = providerId;
        this.type = type;
        this.name = name;
        this.description = description;
        this.address = address;
        this.city = city;
        this.countryCode = countryCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timezone = timezone;
        this.currency = currency;
        this.phone = phone;
        this.email = email;
        this.status = status;
        this.ratingAvg = ratingAvg;
        this.reviewsCount = reviewsCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getProviderId() {
        return providerId;
    }

    public ListingType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public String getTimezone() {
        return timezone;
    }

    public String getCurrency() {
        return currency;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public ListingStatus getStatus() {
        return status;
    }

    public BigDecimal getRatingAvg() {
        return ratingAvg;
    }

    public int getReviewsCount() {
        return reviewsCount;
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
