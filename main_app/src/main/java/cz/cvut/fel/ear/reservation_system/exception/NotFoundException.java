package cz.cvut.fel.ear.reservation_system.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Indicates that a resource was not found.
 */
public class NotFoundException extends ResponseStatusException {

    public NotFoundException(HttpStatus status, String message) {
        super(status, message);
    }
}
