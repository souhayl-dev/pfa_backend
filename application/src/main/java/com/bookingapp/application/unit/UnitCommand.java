package com.bookingapp.application.unit;

import com.bookingapp.domain.unit.UnitDetails;
import com.bookingapp.domain.unit.UnitType;

import java.math.BigDecimal;

/** Used to create and update a unit. details is null for tables and guide services. */
public record UnitCommand(UnitType type, String name, String description, BigDecimal basePrice, int capacity,
                          UnitDetails details) {
}
