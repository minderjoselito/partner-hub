package com.partnerhub.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * Standard error response for API validation and authorization errors.
 */
@Schema(description = "Standard error response for API validation and authorization errors.")
public class ErrorResponseDTO {

    @Schema(description = "Timestamp of the error occurrence", example = "2025-06-15T17:02:00.000+00:00")
    private String timestamp;

    @Schema(description = "HTTP status code", example = "400")
    private int status;

    @Schema(description = "Short description of the error", example = "Unauthorized", nullable = true)
    private String error;

    @Schema(description = "Detailed error message", example = "Full authentication is required to access this resource", nullable = true)
    private String message;

    @Schema(description = "Path of the request that generated the error", example = "/api/users")
    private String path;

    @Schema(description = "List of field errors (for validation errors)")
    private List<FieldError> errors;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<FieldError> getErrors() {
        return errors;
    }

    public void setErrors(List<FieldError> errors) {
        this.errors = errors;
    }

    @Schema(description = "Details about each field error in validation scenarios.")
    public static class FieldError {
        @Schema(description = "Field with the error", example = "email")
        private String field;

        @Schema(description = "Validation message", example = "Email must be valid")
        private String message;

        @Schema(description = "Rejected value for the field", example = "invalid@email")
        private String rejectedValue;

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getRejectedValue() {
            return rejectedValue;
        }

        public void setRejectedValue(String rejectedValue) {
            this.rejectedValue = rejectedValue;
        }
    }
}
