package cz.cvut.fel.ear.reservation_system.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class PaymentNotAllowedException extends ResponseStatusException {
    public PaymentNotAllowedException(HttpStatus status, String message) {
        super(status, message);
    }
}