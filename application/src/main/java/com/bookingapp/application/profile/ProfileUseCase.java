package com.bookingapp.application.profile;

import java.util.UUID;

public interface ProfileUseCase {
    UserView getProfile(UUID userId);

    UserView updateProfile(UpdateProfileCommand command);

    /** Sets the traveller details, creating the client profile if the user does not have one yet. */
    UserView updateClientDetails(UpdateClientDetailsCommand command);

    void changePassword(ChangePasswordCommand command);
}
