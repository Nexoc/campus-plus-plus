package at.campus.backend.common.exception;

/**
 * Thrown when a user attempts an operation
 * they are not allowed to perform.
 *
 * Mapped to HTTP 403 by global exception handler.
 */
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }
}
