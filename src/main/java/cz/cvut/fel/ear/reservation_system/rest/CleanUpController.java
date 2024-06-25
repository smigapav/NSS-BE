package cz.cvut.fel.ear.reservation_system.rest;

import cz.cvut.fel.ear.reservation_system.exception.InvalidApiKeyException;
import cz.cvut.fel.ear.reservation_system.service.CleanUpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest/cron")
@CrossOrigin(origins="*")
@RequiredArgsConstructor
public class CleanUpController {
    private final CleanUpService cleanupService;

    @PostMapping(value = "cleanup")
    public ResponseEntity<Void> deleteNotPaidReservationsLessThanOneDayFromNow(@RequestHeader String apiKey) {
        try {
            cleanupService.deleteNotPaidReservationsLessThanOneDayFromNow(apiKey);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (InvalidApiKeyException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
