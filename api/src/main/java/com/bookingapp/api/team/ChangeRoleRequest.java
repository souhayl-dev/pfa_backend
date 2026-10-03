package com.bookingapp.api.team;

import com.bookingapp.domain.team.MemberRole;
import jakarta.validation.constraints.NotNull;

public record ChangeRoleRequest(@NotNull MemberRole role) {
}
