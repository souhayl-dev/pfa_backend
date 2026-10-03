package com.bookingapp.api.unit;

import com.bookingapp.domain.unit.UnitDetails;
import jakarta.validation.constraints.NotNull;

public record TransportDetailsDto(@NotNull UnitDetails.VehicleType vehicleType) {
}
