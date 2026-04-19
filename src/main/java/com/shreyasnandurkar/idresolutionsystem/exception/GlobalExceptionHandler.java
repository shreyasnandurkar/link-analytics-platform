package com.shreyasnandurkar.idresolutionsystem.exception;

import com.google.zxing.WriterException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.Instant;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ShortKeyNotFoundException.class)
    public ResponseEntity<ApiError> handleShortKeyNotFound(ShortKeyNotFoundException ex, HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleResponseStatus(ResponseStatusException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
        HttpStatus resolvedStatus = status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR;
        String message = ex.getReason() != null ? ex.getReason() : "Request failed";
        return buildError(resolvedStatus, message, request.getRequestURI());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");
        return buildError(HttpStatus.BAD_REQUEST, message, request.getRequestURI());
    }

    @ExceptionHandler({WriterException.class, IOException.class})
    public ResponseEntity<ApiError> handleQrBarcodeExceptions(Exception ex, HttpServletRequest request) {
        log.warn("Media generation failed", ex);
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate media", request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception", ex);
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", request.getRequestURI());
    }

    private ResponseEntity<ApiError> buildError(HttpStatus status, String message, String path) {
        ApiError error = new ApiError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path);
        return ResponseEntity.status(status).body(error);
    }
}
