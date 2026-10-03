package com.bookingapp.api.provider;

import com.bookingapp.application.provider.ProviderDetailsCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Used both to register a provider and to update its details. */
public record ProviderRequest(
        @NotBlank @Size(max = 200) String companyName,
        @Size(max = 200) String legalName,
        @Size(max = 50) String taxId,
        @Size(max = 500) String verificationDocumentUrl,
        @Size(max = 4000) String description) {

    public ProviderDetailsCommand toCommand() {
        return new ProviderDetailsCommand(companyName, legalName, taxId, verificationDocumentUrl, description);
    }
}
