package com.jamith.booksformeapi.utils;

import java.util.regex.Pattern;

public class RequestValidationUtils {

    // Regex patterns
    private static final String EMAIL_PATTERN = "^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$";
    private static final String PHONE_NUMBER_PATTERN = "^\\+?[0-9]{10,15}$";
    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";

    /**
     * Validate an email address.
     *
     * @param email the email to validate
     * @return true if the email is valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        return email != null && Pattern.matches(EMAIL_PATTERN, email);
    }

    /**
     * Validate a phone number.
     *
     * @param phoneNumber the phone number to validate
     * @return true if the phone number is valid, false otherwise
     */
    public static boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber != null && Pattern.matches(PHONE_NUMBER_PATTERN, phoneNumber);
    }

    /**
     * Validate a password.
     * Password must contain:
     * - At least 8 characters
     * - One digit
     * - One lowercase letter
     * - One uppercase letter
     * - One special character
     * - No whitespace allowed
     *
     * @param password the password to validate
     * @return true if the password is valid, false otherwise
     */
    public static boolean isValidPassword(String password) {
        return password != null && Pattern.matches(PASSWORD_PATTERN, password);
    }

    /**
     * Validate a string against a custom regex pattern.
     *
     * @param value the value to validate
     * @param regex the custom regex pattern
     * @return true if the value matches the pattern, false otherwise
     */
    public static boolean isValidPattern(String value, String regex) {
        return value != null && Pattern.matches(regex, value);
    }
}
