package com.bookingapp.api.profile;

import com.bookingapp.application.profile.UserView;
import com.bookingapp.domain.user.Gender;
import com.bookingapp.domain.user.UserRole;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

/** active is false once an admin has deactivated the account. clientId, nationality and birthDate are null until the user has a client profile. */
public record UserResponse(UUID id, String email, String username, String firstName, String lastName, String phone,
                           Gender gender, String profileImage, boolean active, boolean verified,
                           boolean notificationsEnabled, Set<UserRole> roles, UUID clientId, String nationality,
                           LocalDate birthDate) {

    public static UserResponse from(UserView view) {
        return new UserResponse(view.id(), view.email(), view.username(), view.firstName(), view.lastName(),
                view.phone(), view.gender(), view.profileImage(), view.active(), view.verified(),
                view.notificationsEnabled(), view.roles(), view.clientId(), view.nationality(), view.birthDate());
    }
}
