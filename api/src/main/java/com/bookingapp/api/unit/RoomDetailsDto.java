package com.bookingapp.api.unit;

import com.bookingapp.domain.unit.UnitDetails;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RoomDetailsDto(@NotBlank @Size(max = 20) String roomNumber, @NotNull UnitDetails.RoomType roomType) {
}
