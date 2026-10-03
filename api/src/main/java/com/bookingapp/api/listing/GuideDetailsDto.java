package com.bookingapp.api.listing;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record GuideDetailsDto(@Min(0) @Max(80) Integer yearsExperience) {
}
