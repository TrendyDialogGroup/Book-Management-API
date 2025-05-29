package com.bookmanagement.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ISBNGeneratorService - Enhanced for Java 21
 * 
 * @author Book Management Team
 */
@ExtendWith(MockitoExtension.class)
class ISBNGeneratorServiceTest {

    @InjectMocks
    private ISBNGeneratorService isbnGeneratorService;

    @Test
    void testGenerateISBN_ShouldReturnValidISBN() {
        // When
        var isbn = isbnGeneratorService.generateISBN();

        // Then
        assertNotNull(isbn);
        assertEquals(13, isbn.length());
        assertTrue(isbn.startsWith("978"));
        assertTrue(isbnGeneratorService.isValidISBN(isbn));
    }

    @Test
    void testGenerateISBN_ShouldGenerateUniqueISBNs() {
        // When
        var isbn1 = isbnGeneratorService.generateISBN();
        var isbn2 = isbnGeneratorService.generateISBN();

        // Then
        assertNotEquals(isbn1, isbn2);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "9780306406157", // Valid ISBN
        "9781861972712", // Valid ISBN
        "9780134685991"  // Valid ISBN
    })
    void testIsValidISBN_WithValidISBNs_ShouldReturnTrue(String validISBN) {
        var isValid = isbnGeneratorService.isValidISBN(validISBN);
        assertTrue(isValid, "ISBN " + validISBN + " should be valid");
    }

    @ParameterizedTest
    @ValueSource(strings = {"9780306406158", "1234567890123", "9780306406156"})
    void testIsValidISBN_WithInvalidISBNs_ShouldReturnFalse(String invalidISBN) {
        // When
        var isValid = isbnGeneratorService.isValidISBN(invalidISBN);

        // Then
        assertFalse(isValid, "ISBN " + invalidISBN + " should be invalid");
    }

    @Test
    void testIsValidISBN_WithNullISBN_ShouldReturnFalse() {
        // When
        var isValid = isbnGeneratorService.isValidISBN(null);

        // Then
        assertFalse(isValid);
    }

    @ParameterizedTest
    @ValueSource(strings = {"978030640615", "97803064061578", "", "   "})
    void testIsValidISBN_WithWrongLength_ShouldReturnFalse(String wrongLengthISBN) {
        // When
        var isValid = isbnGeneratorService.isValidISBN(wrongLengthISBN);

        // Then
        assertFalse(isValid, "ISBN with wrong length should be invalid: " + wrongLengthISBN);
    }

    @ParameterizedTest
    @ValueSource(strings = {"978030640615A", "978-030-640-157", "978 030 640 157"})
    void testIsValidISBN_WithNonNumericCharacters_ShouldReturnFalse(String nonNumericISBN) {
        // When
        var isValid = isbnGeneratorService.isValidISBN(nonNumericISBN);

        // Then
        assertFalse(isValid, "ISBN with non-numeric characters should be invalid: " + nonNumericISBN);
    }

    @Test
    void testValidateISBNFormat_WithValidISBN_ShouldReturnValidResult() {
        // Given
        var validISBN = "9780306406157";

        // When
        var result = isbnGeneratorService.validateISBNFormat(validISBN);

        // Then
        assertTrue(result.isValid());
        assertEquals("Valid ISBN", result.message());
    }

    @Test
    void testValidateISBNFormat_WithNullISBN_ShouldReturnInvalidResult() {
        // When
        var result = isbnGeneratorService.validateISBNFormat(null);

        // Then
        assertFalse(result.isValid());
        assertEquals("ISBN cannot be null", result.message());
    }

    @Test
    void testValidateISBNFormat_WithBlankISBN_ShouldReturnInvalidResult() {
        // When
        var result = isbnGeneratorService.validateISBNFormat("   ");

        // Then
        assertFalse(result.isValid());
        assertEquals("ISBN cannot be blank", result.message());
    }

    @Test
    void testValidateISBNFormat_WithWrongLength_ShouldReturnInvalidResult() {
        // Given
        var shortISBN = "978030640615";

        // When
        var result = isbnGeneratorService.validateISBNFormat(shortISBN);

        // Then
        assertFalse(result.isValid());
        assertTrue(result.message().contains("must be exactly 13 digits"));
    }

    @Test
    void testValidateISBNFormat_WithWrongPrefix_ShouldReturnInvalidResult() {
        // Given
        var wrongPrefixISBN = "9790306406157";

        // When
        var result = isbnGeneratorService.validateISBNFormat(wrongPrefixISBN);

        // Then
        assertFalse(result.isValid());
        assertTrue(result.message().contains("must start with 978"));
    }

    @Test
    void testISBNValidationResult_WithNullMessage_ShouldThrowException() {
        // When & Then
        assertThrows(IllegalArgumentException.class, 
            () -> new ISBNGeneratorService.ISBNValidationResult(true, null));
    }

    @Test
    void testISBNValidationResult_WithBlankMessage_ShouldThrowException() {
        // When & Then
        assertThrows(IllegalArgumentException.class, 
            () -> new ISBNGeneratorService.ISBNValidationResult(true, "   "));
    }
}