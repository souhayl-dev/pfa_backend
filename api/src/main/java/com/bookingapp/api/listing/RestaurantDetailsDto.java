package com.bookingapp.api.listing;

import jakarta.validation.constraints.Size;

public record RestaurantDetailsDto(@Size(max = 50) String cuisineType) {
}
