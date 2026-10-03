package com.bookingapp.application.admin;

import com.bookingapp.application.profile.UserView;
import com.bookingapp.application.provider.ProviderView;
import com.bookingapp.domain.provider.ProviderStatus;

import java.util.List;
import java.util.UUID;

/** Platform administration. The API only lets users with the ADMIN role reach it. */
public interface AdminUseCase {
    List<ProviderView> providers(ProviderStatus status);

    ProviderView approve(UUID providerId);

    ProviderView reject(UUID providerId);

    ProviderView suspend(UUID providerId);

    List<UserView> users();

    UserView deactivateUser(UUID adminId, UUID userId);

    UserView activateUser(UUID userId);
}
