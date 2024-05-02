package cz.cvut.fel.ear.reservation_system.rest;

import cz.cvut.fel.ear.reservation_system.model.Reservation;
import cz.cvut.fel.ear.reservation_system.model.ReservationStatus;
import cz.cvut.fel.ear.reservation_system.model.Room;
import cz.cvut.fel.ear.reservation_system.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping("/rest/rooms")
@CrossOrigin(origins="*")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Room>> listAllRooms(
            @RequestParam(value = "from", required = false) LocalDateTime fromDate,
            @RequestParam(value = "to", required = false) LocalDateTime toDate,
            Authentication authentication) {

        if (fromDate == null) {
            fromDate = LocalDateTime.now();
        }
        if (toDate == null) {
            toDate = LocalDateTime.now().plusDays(7);
        }

        List<Room> availableRooms = roomService.findAvailableRooms(fromDate, toDate);

        return new ResponseEntity<>(availableRooms, HttpStatus.OK);
    }
}
