package com.bookingapp.api.admin;

import com.bookingapp.api.profile.UserResponse;
import com.bookingapp.api.provider.ProviderResponse;
import com.bookingapp.api.shared.CurrentUser;
import com.bookingapp.application.admin.AdminUseCase;
import com.bookingapp.domain.provider.ProviderStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/** Platform administration. SecurityConfig restricts /api/admin to the ADMIN role. */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminUseCase admin;

    public AdminController(AdminUseCase admin) {
        this.admin = admin;
    }

    @GetMapping("/providers")
    public List<ProviderResponse> providers(@RequestParam(defaultValue = "PENDING") ProviderStatus status) {
        return admin.providers(status).stream().map(ProviderResponse::from).toList();
    }

    @PostMapping("/providers/{providerId}/approve")
    public ProviderResponse approve(@PathVariable UUID providerId) {
        return ProviderResponse.from(admin.approve(providerId));
    }

    @PostMapping("/providers/{providerId}/reject")
    public ProviderResponse reject(@PathVariable UUID providerId) {
        return ProviderResponse.from(admin.reject(providerId));
    }

    @PostMapping("/providers/{providerId}/suspend")
    public ProviderResponse suspend(@PathVariable UUID providerId) {
        return ProviderResponse.from(admin.suspend(providerId));
    }

    @GetMapping("/users")
    public List<UserResponse> users() {
        return admin.users().stream().map(UserResponse::from).toList();
    }

    @PostMapping("/users/{userId}/deactivate")
    public UserResponse deactivate(@PathVariable UUID userId, Authentication authentication) {
        return UserResponse.from(admin.deactivateUser(CurrentUser.id(authentication), userId));
    }

    @PostMapping("/users/{userId}/activate")
    public UserResponse activate(@PathVariable UUID userId) {
        return UserResponse.from(admin.activateUser(userId));
    }
}
