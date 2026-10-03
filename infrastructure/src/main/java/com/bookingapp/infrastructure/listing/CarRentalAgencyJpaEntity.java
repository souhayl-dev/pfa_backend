package com.bookingapp.infrastructure.listing;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "car_rental_agencies")
public class CarRentalAgencyJpaEntity {

    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "listing_id", length = 36)
    private UUID listingId;

    @Column(name = "license_number", nullable = false, length = 100)
    private String licenseNumber;

    @Column(name = "min_driver_age", nullable = false)
    private int minDriverAge;

    @Column(name = "deposit_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal depositAmount;

    protected CarRentalAgencyJpaEntity() {
    }

    public CarRentalAgencyJpaEntity(UUID listingId, String licenseNumber, int minDriverAge, BigDecimal depositAmount) {
        this.listingId = listingId;
        this.licenseNumber = licenseNumber;
        this.minDriverAge = minDriverAge;
        this.depositAmount = depositAmount;
    }

    public UUID getListingId() {
        return listingId;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public int getMinDriverAge() {
        return minDriverAge;
    }

    public BigDecimal getDepositAmount() {
        return depositAmount;
    }
}
