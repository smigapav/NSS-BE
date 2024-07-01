package cz.cvut.fel.ear.reservation_system.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Indicates that insufficient amount of a product is available for processing,
 * e.g. for creating order items.
 */
public class InsufficientAmountException extends ResponseStatusException {

    public InsufficientAmountException(HttpStatus status, String message) {
        super(status, message);
    }
}
