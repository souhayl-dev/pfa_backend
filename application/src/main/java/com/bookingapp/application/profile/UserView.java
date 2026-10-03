package com.bookingapp.application.profile;

import com.bookingapp.domain.client.Client;
import com.bookingapp.domain.user.Gender;
import com.bookingapp.domain.user.User;
import com.bookingapp.domain.user.UserRole;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

/** A deactivated account (active = false) can no longer sign in or act. clientId, nationality and birthDate are null until the user has a client profile. */
public record UserView(UUID id, String email, String username, String firstName, String lastName, String phone,
                       Gender gender, String profileImage, boolean active, boolean verified, String preferredCurrency,
                       boolean notificationsEnabled, Set<UserRole> roles, UUID clientId, String nationality,
                       LocalDate birthDate) {

    public static UserView from(User user, Client client) {
        return new UserView(user.id(), user.email(), user.username(), user.firstName(), user.lastName(),
                user.phone(), user.gender(), user.profileImage(), user.active(), user.verified(), user.preferredCurrency(),
                user.notificationsEnabled(), user.roles(), client == null ? null : client.id(),
                client == null ? null : client.nationality(), client == null ? null : client.birthDate());
    }
}
