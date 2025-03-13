package com.example.supplymaster.error.controller;

import com.example.supplymaster.error.exception.AccessDeniedException;
import com.example.supplymaster.error.exception.EntityNotFoundException;
import com.example.supplymaster.error.exception.PriceConflictException;
import com.example.supplymaster.error.exception.ValidationException;
import com.example.supplymaster.error.model.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Контроллер обработки ошибок, который перехватывает исключения, происходящие в приложении, и возвращает
 * соответствующие ответы с информацией об ошибке.
 * Обрабатывает различные типы исключений, включая ошибки валидации, нарушения целостности данных,
 * ошибки доступа и другие.
 */
@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({ValidationException.class, MissingServletRequestParameterException.class,
            MethodArgumentNotValidException.class, HttpMessageNotReadableException.class,
            InvalidDataAccessResourceUsageException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @Operation(summary = "Обработчик ошибок валидации", description = "Возвращает ошибки, связанные с неверными данными запроса")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации или неверные параметры запроса")
    public ApiError handleBadRequest(final Exception e) {
        log.warn(e.getMessage());
        String errorMessage;
        List<String> errors = new ArrayList<>();
        String reason;

        switch (e) {
            case MethodArgumentNotValidException ex -> {
                errors = ex.getBindingResult().getFieldErrors().stream()
                        .map(fieldError -> String.format("Field '%s': %s", fieldError.getField(),
                                fieldError.getDefaultMessage()))
                        .toList();
                errorMessage = "Validation failed for one or more fields";
                reason = "MethodArgumentNotValidException";
            }
            case MissingServletRequestParameterException ex -> {
                errorMessage = String.format("Required parameter is missing: %s", ex.getParameterName());
                reason = "MissingServletRequestParameterException";
            }
            case HttpMessageNotReadableException ex -> {
                errorMessage = "Malformed JSON request or invalid data type";
                errors.add(ex.getMostSpecificCause().getMessage());
                reason = "HttpMessageNotReadableException";
            }
            case InvalidDataAccessResourceUsageException ex -> {
                errorMessage = "Database access error occurred";
                errors.add(ex.getMostSpecificCause().getMessage());
                reason = "InvalidDataAccessResourceUsageException";
            }
            default -> {
                errorMessage = e.getMessage();
                reason = "ValidationException";
            }
        }
        return ApiError.builder()
                .errors(errors)
                .message(errorMessage)
                .reason(reason)
                .status(HttpStatus.BAD_REQUEST.name())
                .timestamp(OffsetDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @Operation(summary = "Обработчик ошибок поиска", description = "Возвращает ошибку, если сущность не найдена")
    @ApiResponse(responseCode = "404", description = "Сущность не найдена")
    public ApiError handleNotFound(final EntityNotFoundException e) {
        log.warn(e.getMessage());
        List<String> errors = new ArrayList<>();
        return ApiError.builder()
                .errors(errors)
                .message(e.getMessage())
                .reason("EntityNotFoundException")
                .status(HttpStatus.NOT_FOUND.name())
                .timestamp(OffsetDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    @Operation(summary = "Обработчик ошибок целостности данных", description = "Возвращает ошибку, если нарушены ограничения базы данных")
    @ApiResponse(responseCode = "409", description = "Нарушение целостности данных")
    public ApiError handleDataIntegrityViolation(final DataIntegrityViolationException e) {
        log.warn(e.getMessage());
        List<String> errors = new ArrayList<>();
        String errorMessage = "Data integrity violation occurred";
        String reason = "DataIntegrityViolationException";
        errors.add(e.getMostSpecificCause().getMessage());
        return ApiError.builder()
                .errors(errors)
                .message(errorMessage)
                .reason(reason)
                .status(HttpStatus.CONFLICT.name())
                .timestamp(OffsetDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    @Operation(summary = "Обработчик конфликтов цен", description = "Возвращает ошибку при пересечении цен")
    @ApiResponse(responseCode = "409", description = "Конфликт цен")
    public ApiError handlePriceConflict(final PriceConflictException e) {
        log.warn(e.getMessage());
        List<String> errors = new ArrayList<>();
        errors.add(e.getMessage());
        return ApiError.builder()
                .errors(errors)
                .message("Price conflict: overlapping price period detected")
                .reason("PriceConflictException")
                .status(HttpStatus.CONFLICT.name())
                .timestamp(OffsetDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @Operation(summary = "Обработчик ошибок доступа", description = "Возвращает ошибку, если у пользователя нет прав")
    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    public ApiError handleAccessDeniedException(final AccessDeniedException e) {
        log.warn(e.getMessage());
        List<String> errors = new ArrayList<>();
        errors.add(e.getMessage());
        return ApiError.builder()
                .errors(errors)
                .message("Access denied due to insufficient permissions")
                .reason("AccessDeniedException")
                .status(HttpStatus.FORBIDDEN.name())
                .timestamp(OffsetDateTime.now())
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @Operation(summary = "Обработчик неожиданных ошибок", description = "Возвращает ошибку при внутренней ошибке сервера")
    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    public ApiError handleAllUnhandledExceptions(final Exception e) {
        log.error("Unexpected server error: ", e);
        List<String> errors = new ArrayList<>();
        errors.add(e.getMessage());
        return ApiError.builder()
                .errors(errors)
                .message("An unexpected internal server error occurred")
                .reason("InternalServerError")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.name())
                .timestamp(OffsetDateTime.now())
                .build();
    }
}