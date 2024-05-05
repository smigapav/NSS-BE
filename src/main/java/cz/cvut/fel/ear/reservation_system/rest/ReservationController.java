package cz.cvut.fel.ear.reservation_system.rest;

import cz.cvut.fel.ear.reservation_system.dto.ReservationDto;
import cz.cvut.fel.ear.reservation_system.exception.PermissionDeniedException;
import cz.cvut.fel.ear.reservation_system.exception.ReservationConflictException;
import cz.cvut.fel.ear.reservation_system.exception.ReservationNotFoundException;
import cz.cvut.fel.ear.reservation_system.exception.RoomNotAvailableException;
import cz.cvut.fel.ear.reservation_system.model.Reservation;
import cz.cvut.fel.ear.reservation_system.model.ReservationStatus;
import cz.cvut.fel.ear.reservation_system.model.User;
import cz.cvut.fel.ear.reservation_system.rest.util.RestUtils;
import cz.cvut.fel.ear.reservation_system.service.OrderService;
import cz.cvut.fel.ear.reservation_system.service.ReservationService;
import cz.cvut.fel.ear.reservation_system.service.RoomService;
import cz.cvut.fel.ear.reservation_system.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/rest/reservations")
@PreAuthorize("hasAnyAuthority('ADMIN', 'STANDARD_USER')")
@CrossOrigin(origins="*")
public class ReservationController {

    private static final Logger LOG = LoggerFactory.getLogger(ReservationController.class);

    private final ReservationService reservationService;
    private final RoomService roomService;
    private final UserService userService;
    private final OrderService orderService;

    public ReservationController(ReservationService reservationService, RoomService roomService, UserService userService, OrderService orderService) {
        this.reservationService = reservationService;
        this.roomService = roomService;
        this.userService = userService;
        this.orderService = orderService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, value = "create")
    public ResponseEntity<String> createReservation(Authentication authentication, @RequestBody(required = false) Reservation reservation) {
        try {
            String currentUsername = authentication.getName();
            User currentUser = userService.findByUsername(currentUsername);

            Reservation createdReservation = reservationService.createReservationIfRoomAvailable(currentUser, reservation);

            LOG.debug("Created reservation {}.", createdReservation);
            final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/{id}", createdReservation.getId());
            return new ResponseEntity<>(headers, HttpStatus.CREATED);
        } catch (RoomNotAvailableException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, value = "edit")
    public ResponseEntity<String> editReservation(Authentication authentication, @RequestBody(required = false) Reservation reservation) {
        try {
            String currentUsername = authentication.getName();
            User currentUser = userService.findByUsername(currentUsername);

            Reservation updatedReservation = reservationService.editReservationIfPossible(currentUser, reservation);

            LOG.debug("Edited reservation {}.", updatedReservation);
            return new ResponseEntity<>("Reservation successfully edited.", HttpStatus.OK);
        } catch (ReservationNotFoundException | PermissionDeniedException | ReservationConflictException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, value = "storno")
    public ResponseEntity<String> stornoReservation(Principal principal, @RequestBody(required = false) Reservation reservation) {
        try {
            String currentUsername = principal.getName();
            User currentUser = userService.findByUsername(currentUsername);

            Reservation updatedReservation = reservationService.stornoReservationIfPossible(currentUser, reservation);

            LOG.debug("Cancelled reservation {}.", updatedReservation);
            return new ResponseEntity<>("Reservation successfully cancelled.", HttpStatus.OK);
        } catch (ReservationNotFoundException | PermissionDeniedException | IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, value = "pay")
    public ResponseEntity<String> payReservation(Principal principal, @RequestBody(required = false) Reservation reservation) {
        try {
            String currentUsername = principal.getName();
            User currentUser = userService.findByUsername(currentUsername);

            Reservation updatedReservation = reservationService.payReservationIfPossible(currentUser, reservation);

            LOG.debug("Paid reservation {}.", updatedReservation);
            return new ResponseEntity<>("Reservation has been paid successfully.", HttpStatus.OK);
        } catch (ReservationNotFoundException | PermissionDeniedException | IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<ReservationDto> listAllReservations(Authentication authentication) {
        List<Reservation> reservations = Stream.of(reservationService.findByStatus(ReservationStatus.NOT_PAID),
                        reservationService.findByStatus(ReservationStatus.PAID),
                        reservationService.findByStatus(ReservationStatus.STORNO_REQUEST))
                .flatMap(Collection::stream)
                .toList();

        return reservations.stream()
                .map(ReservationDto::new)
                .collect(Collectors.toList());
    }

}

