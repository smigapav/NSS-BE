package cz.cvut.fel.ear.reservation_system.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserNotFoundException extends ResponseStatusException {
    public UserNotFoundException(HttpStatus status, String message) {
        super(status, message);
    }
}
