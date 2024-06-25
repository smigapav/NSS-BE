package cz.cvut.fel.ear.reservation_system.rest;

import cz.cvut.fel.ear.reservation_system.model.Room;
import cz.cvut.fel.ear.reservation_system.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/rest/rooms")
@CrossOrigin(origins="*")
@RequiredArgsConstructor
public class RoomController {

    private static final Logger LOG = LoggerFactory.getLogger(RoomController.class);
    private final RoomService roomService;

    @Cacheable(value = "rooms")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Room>> listAllRooms(
            @RequestParam(value = "from", required = false) LocalDateTime fromDate,
            @RequestParam(value = "to", required = false) LocalDateTime toDate,
            Authentication authentication) {

        List<Room> availableRooms = roomService.findAvailableRooms(fromDate, toDate);

        LOG.info("User {} requested list of available rooms from {} to {}", authentication.getName(), fromDate, toDate);

        return new ResponseEntity<>(availableRooms, HttpStatus.OK);
    }
}
