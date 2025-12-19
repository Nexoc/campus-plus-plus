package at.campus.auth.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

/**
 * ErrorResponse
 *
 * Standard error response returned by the API.
 */
@Schema(description = "Standard API error response")
public class ErrorResponse {

    @Schema(
            description = "Timestamp when the error occurred (ISO-8601)",
            example = "2025-01-10T14:32:45.123Z"
    )
    private String timestamp;

    @Schema(
            description = "HTTP status code",
            example = "401"
    )
    private int status;

    @Schema(
            description = "HTTP status reason phrase",
            example = "Unauthorized"
    )
    private String error;

    @Schema(
            description = "Human-readable error message",
            example = "Invalid credentials"
    )
    private String message;

    @Schema(
            description = "Request path where the error occurred",
            example = "/auth/login"
    )
    private String path;

    @Schema(
            description = "Validation errors mapped by field name",
            example = "{ \"email\": \"must be a well-formed email address\" }",
            nullable = true
    )
    private Map<String, String> fieldErrors;

    public ErrorResponse() {
    }

    private ErrorResponse(Builder builder) {
        this.timestamp = builder.timestamp;
        this.status = builder.status;
        this.error = builder.error;
        this.message = builder.message;
        this.path = builder.path;
        this.fieldErrors = builder.fieldErrors;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }

    /* =========================
       Builder
       ========================= */

    public static class Builder {

        private String timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        private Map<String, String> fieldErrors;

        public Builder timestamp(String timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder status(int status) {
            this.status = status;
            return this;
        }

        public Builder error(String error) {
            this.error = error;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder fieldErrors(Map<String, String> fieldErrors) {
            this.fieldErrors = fieldErrors;
            return this;
        }

        public ErrorResponse build() {
            return new ErrorResponse(this);
        }
    }
}
