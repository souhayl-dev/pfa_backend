package com.bookingapp.infrastructure.provider;

import com.bookingapp.domain.provider.Provider;

public final class ProviderMapper {

    private ProviderMapper() {
    }

    public static Provider toDomain(ProviderJpaEntity e) {
        return new Provider(e.getId(), e.getCompanyName(), e.getLegalName(), e.getTaxId(),
                e.getVerificationDocumentUrl(), e.getDescription(), e.getStatus(), e.getCreatedAt(),
                e.getUpdatedAt());
    }

    public static ProviderJpaEntity toEntity(Provider p) {
        return new ProviderJpaEntity(p.id(), p.companyName(), p.legalName(), p.taxId(), p.verificationDocumentUrl(),
                p.description(), p.status(), p.createdAt(), p.updatedAt());
    }
}
