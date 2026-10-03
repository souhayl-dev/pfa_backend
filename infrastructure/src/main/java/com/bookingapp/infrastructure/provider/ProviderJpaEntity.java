package com.bookingapp.infrastructure.provider;

import com.bookingapp.domain.provider.ProviderStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "providers")
public class ProviderJpaEntity {

    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(length = 36)
    private UUID id;

    @Column(name = "company_name", nullable = false, length = 200)
    private String companyName;

    @Column(name = "legal_name", length = 200)
    private String legalName;

    @Column(name = "tax_id", length = 50)
    private String taxId;

    @Column(name = "verification_document_url", length = 500)
    private String verificationDocumentUrl;

    @Column(length = 4000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProviderStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected ProviderJpaEntity() {
    }

    public ProviderJpaEntity(UUID id, String companyName, String legalName, String taxId,
                             String verificationDocumentUrl, String description, ProviderStatus status,
                             Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.companyName = companyName;
        this.legalName = legalName;
        this.taxId = taxId;
        this.verificationDocumentUrl = verificationDocumentUrl;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getLegalName() {
        return legalName;
    }

    public String getTaxId() {
        return taxId;
    }

    public String getVerificationDocumentUrl() {
        return verificationDocumentUrl;
    }

    public String getDescription() {
        return description;
    }

    public ProviderStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
