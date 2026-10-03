package com.bookingapp.domain.provider;

import com.bookingapp.domain.shared.exception.BusinessRuleException;
import com.bookingapp.domain.shared.Require;

import java.time.Instant;
import java.util.UUID;

/** A business on the platform. Its people are ProviderMembers; what it offers are Listings. */
public class Provider {

    private final UUID id;
    private String companyName;
    private String legalName;
    private String taxId;
    private String verificationDocumentUrl;
    private String description;
    private ProviderStatus status;
    private final Instant createdAt;
    private Instant updatedAt;

    public Provider(UUID id, String companyName, String legalName, String taxId, String verificationDocumentUrl,
                    String description, ProviderStatus status, Instant createdAt, Instant updatedAt) {
        this.id = Require.notNull(id, "id");
        this.companyName = Require.notBlank(companyName, "companyName", 200);
        this.legalName = Require.optional(legalName, "legalName", 200);
        this.taxId = Require.optional(taxId, "taxId", 50);
        this.verificationDocumentUrl = Require.optional(verificationDocumentUrl, "verificationDocumentUrl", 500);
        this.description = Require.optional(description, "description", 4000);
        this.status = Require.notNull(status, "status");
        this.createdAt = Require.notNull(createdAt, "createdAt");
        this.updatedAt = Require.notNull(updatedAt, "updatedAt");
    }

    /** A new provider waits in PENDING until an admin reviews it. */
    public static Provider register(UUID id, String companyName, String legalName, String taxId,
                                    String verificationDocumentUrl, String description, Instant now) {
        return new Provider(id, companyName, legalName, taxId, verificationDocumentUrl, description,
                ProviderStatus.PENDING, now, now);
    }

    public void updateDetails(String companyName, String legalName, String taxId, String verificationDocumentUrl,
                              String description, Instant now) {
        this.companyName = Require.notBlank(companyName, "companyName", 200);
        this.legalName = Require.optional(legalName, "legalName", 200);
        this.taxId = Require.optional(taxId, "taxId", 50);
        this.verificationDocumentUrl = Require.optional(verificationDocumentUrl, "verificationDocumentUrl", 500);
        this.description = Require.optional(description, "description", 4000);
        this.updatedAt = now;
    }

    public void approve(Instant now) {
        if (status != ProviderStatus.PENDING && status != ProviderStatus.SUSPENDED) {
            throw new BusinessRuleException("only a pending or suspended provider can be approved");
        }
        changeStatus(ProviderStatus.APPROVED, now);
    }

    public void reject(Instant now) {
        if (status != ProviderStatus.PENDING) {
            throw new BusinessRuleException("only a pending provider can be rejected");
        }
        changeStatus(ProviderStatus.REJECTED, now);
    }

    public void suspend(Instant now) {
        if (status != ProviderStatus.APPROVED) {
            throw new BusinessRuleException("only an approved provider can be suspended");
        }
        changeStatus(ProviderStatus.SUSPENDED, now);
    }

    private void changeStatus(ProviderStatus newStatus, Instant now) {
        this.status = newStatus;
        this.updatedAt = now;
    }

    /** Listings of a provider are only visible and bookable while it is approved. */
    public boolean isApproved() {
        return status == ProviderStatus.APPROVED;
    }

    public UUID id() {
        return id;
    }

    public String companyName() {
        return companyName;
    }

    public String legalName() {
        return legalName;
    }

    public String taxId() {
        return taxId;
    }

    public String verificationDocumentUrl() {
        return verificationDocumentUrl;
    }

    public String description() {
        return description;
    }

    public ProviderStatus status() {
        return status;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
