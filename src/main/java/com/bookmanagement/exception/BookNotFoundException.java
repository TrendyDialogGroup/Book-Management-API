package com.bookmanagement.exception;

/**
 * Exception thrown when a book is not found
 * 
 * @author Book Management Team
 */
public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(String message) {
        super(message);
    }

    public BookNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}