package com.bookingapp.api.auth;

import com.bookingapp.api.profile.UserResponse;
import com.bookingapp.application.auth.LoginCommand;
import com.bookingapp.application.auth.RegisterUserCommand;
import com.bookingapp.application.auth.RequestPasswordResetCommand;
import com.bookingapp.application.auth.ResetPasswordCommand;
import com.bookingapp.application.auth.VerifyEmailCommand;
import com.bookingapp.application.auth.AuthUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.bookingapp.api.shared.CurrentUser;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthUseCase authUseCase;

    public AuthController(AuthUseCase authUseCase) {
        this.authUseCase = authUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        var view = authUseCase.register(new RegisterUserCommand(request.firstName(), request.lastName(),
                request.email(), request.password()));
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.from(view));
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        var result = authUseCase.login(new LoginCommand(request.email(), request.password()));
        return AuthResponse.from(result);
    }

    @PostMapping("/verify-email")
    public void verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        authUseCase.verifyEmail(new VerifyEmailCommand(request.token()));
    }

    /** For a signed-in user who lost the first email or let its link expire. */
    @PostMapping("/resend-verification")
    public void resendVerification(Authentication authentication) {
        authUseCase.resendVerification(CurrentUser.id(authentication));
    }

    @PostMapping("/request-password-reset")
    public void requestPasswordReset(@Valid @RequestBody RequestPasswordResetRequest request) {
        authUseCase.requestPasswordReset(new RequestPasswordResetCommand(request.email()));
    }

    @PostMapping("/reset-password")
    public void resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authUseCase.resetPassword(new ResetPasswordCommand(request.token(), request.newPassword()));
    }
}
