package com.bookingapp.application.auth;

import com.bookingapp.application.profile.UserView;

import java.util.UUID;

public interface AuthUseCase {
    UserView register(RegisterUserCommand command);

    AuthResult login(LoginCommand command);

    void verifyEmail(VerifyEmailCommand command);

    /** Sends a new verification link to a signed-in user whose email is not verified yet. */
    void resendVerification(UUID userId);

    void requestPasswordReset(RequestPasswordResetCommand command);

    void resetPassword(ResetPasswordCommand command);
}
