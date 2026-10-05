package com.bookingapp.api.profile;

import com.bookingapp.api.shared.CurrentUser;
import com.bookingapp.application.profile.ChangePasswordCommand;
import com.bookingapp.application.profile.ProfileUseCase;
import com.bookingapp.application.profile.UpdateClientDetailsCommand;
import com.bookingapp.application.profile.UpdateProfileCommand;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final ProfileUseCase profileUseCase;

    public ProfileController(ProfileUseCase profileUseCase) {
        this.profileUseCase = profileUseCase;
    }

    @GetMapping("/me")
    public UserResponse getMyProfile(Authentication authentication) {
        return UserResponse.from(profileUseCase.getProfile(CurrentUser.id(authentication)));
    }

    @PatchMapping("/me")
    public UserResponse updateMyProfile(@Valid @RequestBody UpdateProfileRequest request,
                                        Authentication authentication) {
        return UserResponse.from(profileUseCase.updateProfile(new UpdateProfileCommand(
                CurrentUser.id(authentication), request.firstName(), request.lastName(), request.username(),
                request.phone(), request.gender(), request.notificationsEnabled(),
                request.profileImage())));
    }

    /** The traveller details kept on the client profile. */
    @PutMapping("/me/client")
    public UserResponse updateMyClientDetails(@Valid @RequestBody UpdateClientDetailsRequest request,
                                              Authentication authentication) {
        return UserResponse.from(profileUseCase.updateClientDetails(new UpdateClientDetailsCommand(
                CurrentUser.id(authentication), request.nationality(), request.birthDate())));
    }

    @PostMapping("/me/change-password")
    public void changePassword(@Valid @RequestBody ChangePasswordRequest request, Authentication authentication) {
        profileUseCase.changePassword(new ChangePasswordCommand(CurrentUser.id(authentication),
                request.currentPassword(), request.newPassword()));
    }
}
