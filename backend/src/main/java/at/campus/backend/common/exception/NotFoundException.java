package at.campus.backend.common.exception;

/**
 * Thrown when a requested resource does not exist.
 *
 * This exception is part of the application-level
 * error model and MUST be translated to HTTP 404
 * by a global @ControllerAdvice.
 *
 * IMPORTANT:
 * - Services throw this exception
 * - Controllers do NOT catch it
 * - Repository does NOT throw it
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
