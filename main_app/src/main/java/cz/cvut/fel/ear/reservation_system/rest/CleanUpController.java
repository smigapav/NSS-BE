package cz.cvut.fel.ear.reservation_system.rest;

import cz.cvut.fel.ear.reservation_system.exception.InvalidApiKeyException;
import cz.cvut.fel.ear.reservation_system.service.CleanUpService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest/cron")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CleanUpController {
    private final CleanUpService cleanupService;
    private static final Logger LOG = LoggerFactory.getLogger(CleanUpController.class);


    @PostMapping(value = "cleanup")
    public ResponseEntity<Void> deleteNotPaidReservationsLessThanOneDayFromNow(@RequestHeader String apiKey) {
        try {
            cleanupService.deleteNotPaidReservationsLessThanOneDayFromNow(apiKey);
            LOG.info("Deleted all not paid reservations less than one day from now.");
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (InvalidApiKeyException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
