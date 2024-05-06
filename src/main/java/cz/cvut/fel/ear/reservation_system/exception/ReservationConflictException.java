package cz.cvut.fel.ear.reservation_system.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ReservationConflictException extends ResponseStatusException {
    public ReservationConflictException(HttpStatus status, String message) {
        super(status, message);
    }
}
