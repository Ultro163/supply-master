package com.example.supplymaster.error.exception;

/**
 * Исключение, выбрасываемое при отсутствии запрашиваемой сущности в системе.
 */
public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}