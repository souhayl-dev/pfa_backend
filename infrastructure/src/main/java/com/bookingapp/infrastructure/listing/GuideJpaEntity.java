package com.bookingapp.infrastructure.listing;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "guides")
public class GuideJpaEntity {

    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "listing_id", length = 36)
    private UUID listingId;

    @Column(name = "years_experience")
    private Integer yearsExperience;

    protected GuideJpaEntity() {
    }

    public GuideJpaEntity(UUID listingId, Integer yearsExperience) {
        this.listingId = listingId;
        this.yearsExperience = yearsExperience;
    }

    public UUID getListingId() {
        return listingId;
    }

    public Integer getYearsExperience() {
        return yearsExperience;
    }
}
