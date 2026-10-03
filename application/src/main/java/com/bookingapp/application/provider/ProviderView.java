package com.bookingapp.application.provider;

import com.bookingapp.domain.provider.Provider;
import com.bookingapp.domain.provider.ProviderStatus;

import java.time.Instant;
import java.util.UUID;

public record ProviderView(UUID id, String companyName, String legalName, String taxId,
                           String verificationDocumentUrl, String description, ProviderStatus status,
                           Instant createdAt) {

    public static ProviderView from(Provider p) {
        return new ProviderView(p.id(), p.companyName(), p.legalName(), p.taxId(), p.verificationDocumentUrl(),
                p.description(), p.status(), p.createdAt());
    }
}
