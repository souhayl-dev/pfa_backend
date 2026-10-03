package com.bookingapp.api.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/** Used both to write a review and to edit it. */
public record ReviewRequest(@Min(1) @Max(5) int rating, @Size(max = 2000) String comment) {
}
