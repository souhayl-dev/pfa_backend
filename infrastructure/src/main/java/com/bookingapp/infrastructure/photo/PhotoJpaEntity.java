package com.bookingapp.infrastructure.photo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

/** Exactly one of listingId and unitId is set; chk_photos_owner enforces it. */
@Entity
@Table(name = "photos")
public class PhotoJpaEntity {

    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(length = 36)
    private UUID id;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "listing_id", length = 36)
    private UUID listingId;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "unit_id", length = 36)
    private UUID unitId;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected PhotoJpaEntity() {
    }

    public PhotoJpaEntity(UUID id, UUID listingId, UUID unitId, String url, int sortOrder, Instant createdAt) {
        this.id = id;
        this.listingId = listingId;
        this.unitId = unitId;
        this.url = url;
        this.sortOrder = sortOrder;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getListingId() {
        return listingId;
    }

    public UUID getUnitId() {
        return unitId;
    }

    public String getUrl() {
        return url;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
