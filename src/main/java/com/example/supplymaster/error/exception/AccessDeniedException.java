package com.example.supplymaster.error.exception;

/**
 * Исключение, выбрасываемое при отказе в доступе.
 */
public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String message) {
        super(message);
    }
}