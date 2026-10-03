package com.bookingapp.domain.shared;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.regex.Pattern;

/** Small argument checks shared by the domain model. Violations throw IllegalArgumentException. */
public final class Require {

    private static final Pattern CURRENCY = Pattern.compile("[A-Z]{3}");
    private static final Pattern COUNTRY = Pattern.compile("[A-Z]{2}");
    private static final Pattern LANGUAGE = Pattern.compile("[a-z]{2}");

    private Require() {
    }

    public static <T> T notNull(T value, String field) {
        return Objects.requireNonNull(value, field + " is required");
    }

    public static String notBlank(String value, String field, int maxLength) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
        return maxLength(value.strip(), field, maxLength);
    }

    /** Blank becomes null, anything else is stripped and length-checked. */
    public static String optional(String value, String field, int maxLength) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return maxLength(value.strip(), field, maxLength);
    }

    public static String maxLength(String value, String field, int maxLength) {
        if (value != null && value.length() > maxLength) {
            throw new IllegalArgumentException(field + " must be at most " + maxLength + " characters");
        }
        return value;
    }

    public static int positive(int value, String field) {
        if (value <= 0) {
            throw new IllegalArgumentException(field + " must be positive");
        }
        return value;
    }

    public static int between(int value, int min, int max, String field) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(field + " must be between " + min + " and " + max);
        }
        return value;
    }

    public static BigDecimal nonNegative(BigDecimal value, String field) {
        notNull(value, field);
        if (value.signum() < 0) {
            throw new IllegalArgumentException(field + " cannot be negative");
        }
        return value;
    }

    public static String currency(String value) {
        if (value == null || !CURRENCY.matcher(value).matches()) {
            throw new IllegalArgumentException("currency must be a 3-letter ISO code such as EUR");
        }
        return value;
    }

    public static String countryCode(String value, String field) {
        if (value == null || !COUNTRY.matcher(value).matches()) {
            throw new IllegalArgumentException(field + " must be a 2-letter ISO country code such as MA");
        }
        return value;
    }

    public static String languageCode(String value) {
        if (value == null || !LANGUAGE.matcher(value).matches()) {
            throw new IllegalArgumentException("language must be a 2-letter ISO 639-1 code such as fr");
        }
        return value;
    }

    public static String email(String value, String field) {
        String email = notBlank(value, field, 255).toLowerCase();
        int at = email.indexOf('@');
        if (at <= 0 || at != email.lastIndexOf('@') || at == email.length() - 1) {
            throw new IllegalArgumentException(field + " must be a valid email address");
        }
        return email;
    }
}
