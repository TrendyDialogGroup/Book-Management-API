package com.bookmanagement.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.stream.IntStream;

/**
 * Service for generating ISBN-13 numbers with proper check digit calculation
 * Enhanced for Java 21 with modern language features
 * 
 * @author Book Management Team
 */
@Service
public class ISBNGeneratorService {

    private static final String ISBN_PREFIX = "978";
    private static final int ISBN_LENGTH = 13;
    private static final int CHECK_DIGIT_POSITION = 12;
    private static final SecureRandom random = new SecureRandom();

    /**
     * Generates a valid ISBN-13 number using Java 21 features
     * @return a 13-digit ISBN string with proper check digit
     */
    public String generateISBN() {
        // Generate first 12 digits (978 + 9 random digits) using Java 21 string templates
        var isbn12 = ISBN_PREFIX + generateRandomDigits(9);
        
        // Calculate and append check digit
        var checkDigit = calculateCheckDigit(isbn12);
        
        return isbn12 + checkDigit;
    }

    /**
     * Generates random digits using Java 21 streams and text blocks for better readability
     * @param count number of digits to generate
     * @return string of random digits
     */
    private String generateRandomDigits(int count) {
        return IntStream.range(0, count)
                .map(i -> random.nextInt(10))
                .mapToObj(String::valueOf)
                .reduce("", String::concat);
    }

    /**
     * Calculates the check digit for ISBN-13 using enhanced switch expressions
     * Formula: Sum = (first digit * 1) + (second digit * 3) + (third digit * 1) + ... + (twelfth digit * 3)
     * Check digit = (10 - (Sum % 10)) % 10
     * 
     * @param isbn12 the first 12 digits of the ISBN
     * @return the calculated check digit
     */
    private int calculateCheckDigit(String isbn12) {
        if (isbn12 == null || isbn12.length() != CHECK_DIGIT_POSITION) {
            throw new IllegalArgumentException(
                "ISBN must have exactly " + CHECK_DIGIT_POSITION +
                " digits for check digit calculation, got: " + (isbn12 != null ? isbn12.length() : null)
            );
        }

        var sum = IntStream.range(0, CHECK_DIGIT_POSITION)
                .map(i -> {
                    var digit = Character.getNumericValue(isbn12.charAt(i));
                    var multiplier = switch (i % 2) {
                        case 0 -> 1;  // Odd positions (0, 2, 4, ...)
                        case 1 -> 3;  // Even positions (1, 3, 5, ...)
                        default -> throw new IllegalStateException("Unexpected value: " + (i % 2));
                    };
                    return digit * multiplier;
                })
                .sum();

        return (10 - (sum % 10)) % 10;
    }

    /**
     * Validates if an ISBN-13 has the correct check digit using pattern matching
     * @param isbn the complete 13-digit ISBN
     * @return true if valid, false otherwise
     */
    public boolean isValidISBN(String isbn) {
        return switch (isbn) {
            case null -> false;
            case String s when s.length() != ISBN_LENGTH -> false;
            case String s when !s.matches("\\d{13}") -> false;
            case String s -> {
                try {
                    var isbn12 = s.substring(0, CHECK_DIGIT_POSITION);
                    var providedCheckDigit = Character.getNumericValue(s.charAt(CHECK_DIGIT_POSITION));
                    var calculatedCheckDigit = calculateCheckDigit(isbn12);
                    yield providedCheckDigit == calculatedCheckDigit;
                } catch (NumberFormatException e) {
                    yield false;
                }
            }
        };
    }

    /**
     * Validates ISBN format using Java 21 pattern matching
     * @param isbn the ISBN to validate
     * @return validation result with details
     */
    public ISBNValidationResult validateISBNFormat(String isbn) {
        return switch (isbn) {
            case null -> new ISBNValidationResult(false, "ISBN cannot be null");
            case String s when s.isBlank() -> new ISBNValidationResult(false, "ISBN cannot be blank");
            case String s when s.length() != ISBN_LENGTH -> 
                new ISBNValidationResult(false, "ISBN must be exactly " + ISBN_LENGTH + " digits, got " + s.length());
            case String s when !s.matches("\\d{13}") -> 
                new ISBNValidationResult(false, "ISBN must contain only digits");
            case String s when !s.startsWith(ISBN_PREFIX) -> 
                new ISBNValidationResult(false, "ISBN must start with " + ISBN_PREFIX);
            case String s when !isValidISBN(s) -> 
                new ISBNValidationResult(false, "Invalid check digit");
            default -> new ISBNValidationResult(true, "Valid ISBN");
        };
    }

    /**
     * Record for ISBN validation result - using Java 21 records
     */
    public record ISBNValidationResult(boolean isValid, String message) {
        public ISBNValidationResult {
            // Compact constructor with validation
            if (message == null || message.isBlank()) {
                throw new IllegalArgumentException("Message cannot be null or blank");
            }
        }
    }
}