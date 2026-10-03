package com.bookingapp.api.team;

import com.bookingapp.domain.team.MemberRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** The email of a person who already has an account. */
public record AddMemberRequest(@NotBlank @Email String email, @NotNull MemberRole role) {
}
