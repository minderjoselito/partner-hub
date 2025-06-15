package com.partnerhub.exception;

import com.partnerhub.dto.ErrorResponseDTO;
import com.partnerhub.dto.ErrorResponseDTO.FieldError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles exceptions globally and returns meaningful HTTP responses.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> {
                    FieldError fe = new FieldError();
                    fe.setField(error.getField());
                    fe.setMessage(error.getDefaultMessage());
                    fe.setRejectedValue(error.getRejectedValue() != null ? error.getRejectedValue().toString() : null);
                    return fe;
                })
                .collect(Collectors.toList());

        ErrorResponseDTO response = new ErrorResponseDTO();
        response.setTimestamp(DateTimeFormatter.ISO_INSTANT.format(Instant.now()));
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
        response.setPath(request.getRequestURI());
        response.setErrors(fieldErrors);

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {
        ErrorResponseDTO response = new ErrorResponseDTO();
        response.setTimestamp(DateTimeFormatter.ISO_INSTANT.format(Instant.now()));
        response.setStatus(HttpStatus.CONFLICT.value());
        response.setError(HttpStatus.CONFLICT.getReasonPhrase());
        response.setMessage(ex.getMessage());
        response.setPath(request.getRequestURI());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFoundException(
            NotFoundException ex,
            HttpServletRequest request
    ) {
        ErrorResponseDTO response = new ErrorResponseDTO();
        response.setTimestamp(DateTimeFormatter.ISO_INSTANT.format(Instant.now()));
        response.setStatus(HttpStatus.NOT_FOUND.value());
        response.setError(HttpStatus.NOT_FOUND.getReasonPhrase());
        response.setMessage(ex.getMessage());
        response.setPath(request.getRequestURI());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneralException(
            Exception ex,
            HttpServletRequest request
    ) {
        ErrorResponseDTO response = new ErrorResponseDTO();
        response.setTimestamp(DateTimeFormatter.ISO_INSTANT.format(Instant.now()));
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setError(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        response.setMessage("An unexpected error occurred.");
        response.setPath(request.getRequestURI());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
