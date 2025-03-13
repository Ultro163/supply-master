package com.example.supplymaster.error.exception;

/**
 * Исключение, выбрасываемое при ошибках валидации данных.
 */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}