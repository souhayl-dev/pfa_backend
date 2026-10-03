package com.bookingapp.application.provider;

/** Used both to register a provider and to update its details. */
public record ProviderDetailsCommand(String companyName, String legalName, String taxId,
                                     String verificationDocumentUrl, String description) {
}
