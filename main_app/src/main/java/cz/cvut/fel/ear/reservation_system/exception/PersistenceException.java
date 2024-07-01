package cz.cvut.fel.ear.reservation_system.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class PersistenceException extends ResponseStatusException {

    public PersistenceException(HttpStatus status, String message, Throwable cause) {
        super(status, message, cause);
    }
}
