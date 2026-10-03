package com.bookingapp.infrastructure.unit;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public class TourStepEmbeddable {

    @Column(name = "step_order", nullable = false)
    private int stepOrder;

    @Column(name = "day_number", nullable = false)
    private int dayNumber;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(length = 1000)
    private String description;

    protected TourStepEmbeddable() {
    }

    public TourStepEmbeddable(int stepOrder, int dayNumber, String city, String description) {
        this.stepOrder = stepOrder;
        this.dayNumber = dayNumber;
        this.city = city;
        this.description = description;
    }

    public int getStepOrder() {
        return stepOrder;
    }

    public int getDayNumber() {
        return dayNumber;
    }

    public String getCity() {
        return city;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof TourStepEmbeddable that
                && stepOrder == that.stepOrder
                && dayNumber == that.dayNumber
                && Objects.equals(city, that.city)
                && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stepOrder, dayNumber, city, description);
    }
}
