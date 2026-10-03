package com.bookingapp.infrastructure.unit;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/** The steps are rows of tour_steps, keyed by (tour_id, step_order). */
@Entity
@Table(name = "tours")
public class TourJpaEntity {

    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "unit_id", length = 36)
    private UUID unitId;

    @Column(name = "duration_days", nullable = false)
    private int durationDays;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "tour_steps", joinColumns = @JoinColumn(name = "tour_id"))
    private Set<TourStepEmbeddable> steps = new HashSet<>();

    protected TourJpaEntity() {
    }

    public TourJpaEntity(UUID unitId, int durationDays, Set<TourStepEmbeddable> steps) {
        this.unitId = unitId;
        this.durationDays = durationDays;
        this.steps = new HashSet<>(steps);
    }

    public UUID getUnitId() {
        return unitId;
    }

    public int getDurationDays() {
        return durationDays;
    }

    public Set<TourStepEmbeddable> getSteps() {
        return steps;
    }
}
