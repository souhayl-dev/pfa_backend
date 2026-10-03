package com.bookingapp.api.provider;

import com.bookingapp.api.shared.CurrentUser;
import com.bookingapp.application.provider.ProviderUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/providers")
public class ProviderController {

    private final ProviderUseCase providers;

    public ProviderController(ProviderUseCase providers) {
        this.providers = providers;
    }

    /** Any signed-in user can start a business. It is created PENDING, with the caller as its owner. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProviderResponse register(@Valid @RequestBody ProviderRequest request, Authentication authentication) {
        return ProviderResponse.from(providers.register(CurrentUser.id(authentication), request.toCommand()));
    }

    /** The providers the caller works for, for the "switch account" menu. */
    @GetMapping("/mine")
    public List<MembershipResponse> mine(Authentication authentication) {
        return providers.myMemberships(CurrentUser.id(authentication)).stream()
                .map(MembershipResponse::from)
                .toList();
    }

    @GetMapping("/{providerId}")
    public ProviderResponse get(@PathVariable UUID providerId, Authentication authentication) {
        return ProviderResponse.from(providers.get(CurrentUser.id(authentication), providerId));
    }

    @PutMapping("/{providerId}")
    public ProviderResponse update(@PathVariable UUID providerId, @Valid @RequestBody ProviderRequest request,
                                   Authentication authentication) {
        return ProviderResponse.from(
                providers.update(CurrentUser.id(authentication), providerId, request.toCommand()));
    }
}
