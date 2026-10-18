package com.earmarked.earmark.config;

import com.earmarked.earmark.dto.ApiError;
import com.earmarked.earmark.exception.ApiException;
import com.earmarked.earmark.exception.album.AlbumServiceUnavailableException;
import feign.RetryableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.core.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class EarmarkExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiError> handleApiException(ApiException e) {
        return ResponseEntity.status(e.getStatus()).body(new ApiError(e.getCode(), e.getMessage(), Map.of()));
    }

    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<ApiError> handleInvalidSort(PropertyReferenceException e) {
        return ResponseEntity.badRequest()
                .body(new ApiError("INVALID_SORT", "Invalid sort property", Map.of()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException exception) {
        Map<String, String> fieldErrors = exception.getBindingResult().getFieldErrors().stream().collect(
                Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage,
                        (existing, replacement) -> existing));
        ApiError error = new ApiError("VALIDATION_ERROR", null, fieldErrors);

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleMessageNotReadable(HttpMessageNotReadableException exception) {
        ApiError error = new ApiError("INVALID_REQUEST", "Request body is missing or malformed", Map.of());

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(RetryableException.class)
    public ResponseEntity<ApiError> handleRetryable(RetryableException e) {
        return handleApiException(new AlbumServiceUnavailableException());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpectedException(Exception exception) {
        log.error("Unexpected error occurred", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiError("INTERNAL_SERVER_ERROR", "An unexpected error occurred",
                        Map.of()));
    }
}
