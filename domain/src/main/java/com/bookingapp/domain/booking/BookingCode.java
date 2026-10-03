package com.bookingapp.domain.booking;

import java.util.random.RandomGenerator;
import java.util.regex.Pattern;

/** Short reference a client can read over the phone, such as BK-7Q2M4X. */
public final class BookingCode {

    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final Pattern FORMAT = Pattern.compile("BK-[A-Z0-9]{6}");

    private BookingCode() {
    }

    /** Leaves out 0/O and 1/I, which are easy to confuse. Pass a SecureRandom so codes are unguessable. */
    public static String generate(RandomGenerator random) {
        StringBuilder code = new StringBuilder("BK-");
        for (int i = 0; i < 6; i++) {
            code.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return code.toString();
    }

    public static String validate(String code) {
        if (code == null || !FORMAT.matcher(code).matches()) {
            throw new IllegalArgumentException("booking code must look like BK-7Q2M4X");
        }
        return code;
    }
}
