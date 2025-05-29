package com.bookmanagement.controller;

import com.bookmanagement.exception.BookNotFoundException;
import com.bookmanagement.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for GlobalExceptionHandler
 * 
 * @author Book Management Team
 */
@ActiveProfiles("test")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void testHandleBookNotFoundException() {
        // Given
        var exception = new BookNotFoundException("Book not found with id: 1");

        // When
        var response = globalExceptionHandler.handleBookNotFoundException(exception);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Book not found with id: 1", response.getBody().getMessage());
    }

    @Test
    void testHandleValidationExceptions() {
        // Given
        var bindingResult = mock(BindingResult.class);
        var fieldError = new FieldError("bookDTO", "title", "Title cannot be blank");
        when(bindingResult.getAllErrors()).thenReturn(Arrays.asList(fieldError));
        
        var exception = new MethodArgumentNotValidException(null, bindingResult);

        // When
        var response = globalExceptionHandler.handleValidationExceptions(exception);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Validation failed", response.getBody().getMessage());
        assertTrue(response.getBody().getFieldErrors().containsKey("title"));
    }

    @Test
    void testHandleGenericException() {
        // Given
        var exception = new RuntimeException("Unexpected error");

        // When
        var response = globalExceptionHandler.handleGenericException(exception);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("An unexpected error occurred", response.getBody().getMessage());
    }
}