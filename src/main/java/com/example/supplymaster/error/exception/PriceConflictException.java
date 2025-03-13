package com.example.supplymaster.error.exception;

/**
 * Исключение, выбрасываемое при конфликте цен, например,
 * при попытке добавить цену с пересекающимся диапазоном дат.
 */
public class PriceConflictException extends RuntimeException {
    public PriceConflictException(String message) {
        super(message);
    }
}
