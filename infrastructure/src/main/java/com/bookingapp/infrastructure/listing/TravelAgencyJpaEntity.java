package com.bookingapp.infrastructure.listing;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "travel_agencies")
public class TravelAgencyJpaEntity {

    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "listing_id", length = 36)
    private UUID listingId;

    @Column(name = "license_number", nullable = false, length = 100)
    private String licenseNumber;

    protected TravelAgencyJpaEntity() {
    }

    public TravelAgencyJpaEntity(UUID listingId, String licenseNumber) {
        this.listingId = listingId;
        this.licenseNumber = licenseNumber;
    }

    public UUID getListingId() {
        return listingId;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }
}
