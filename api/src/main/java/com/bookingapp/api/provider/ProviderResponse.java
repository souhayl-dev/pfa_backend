package com.bookingapp.api.provider;

import com.bookingapp.application.provider.ProviderView;
import com.bookingapp.domain.provider.ProviderStatus;

import java.time.Instant;
import java.util.UUID;

public record ProviderResponse(UUID id, String companyName, String legalName, String taxId,
                               String verificationDocumentUrl, String description, ProviderStatus status,
                               Instant createdAt) {

    public static ProviderResponse from(ProviderView view) {
        return new ProviderResponse(view.id(), view.companyName(), view.legalName(), view.taxId(),
                view.verificationDocumentUrl(), view.description(), view.status(), view.createdAt());
    }
}
