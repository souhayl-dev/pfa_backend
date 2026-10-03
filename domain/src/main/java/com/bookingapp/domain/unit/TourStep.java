package com.bookingapp.domain.unit;

import com.bookingapp.domain.shared.Require;

public record TourStep(int stepOrder, int dayNumber, String city, String description) {

    public TourStep {
        Require.positive(stepOrder, "stepOrder");
        Require.positive(dayNumber, "dayNumber");
        city = Require.notBlank(city, "city", 100);
        description = Require.optional(description, "description", 1000);
    }
}
