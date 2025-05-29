package com.bookmanagement.exception;

/**
 * Exception thrown when attempting to create a book with duplicate ISBN
 * 
 * @author Book Management Team
 */
public class DuplicateISBNException extends RuntimeException {

    public DuplicateISBNException(String message) {
        super(message);
    }

    public DuplicateISBNException(String message, Throwable cause) {
        super(message, cause);
    }
}