package cz.cvut.fel.ear.reservation_system.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Signifies that invalid data have been provided to the application.
 */
public class ValidationException extends ResponseStatusException {

    public ValidationException(HttpStatus status, String message) {
        super(status, message);
    }
}
