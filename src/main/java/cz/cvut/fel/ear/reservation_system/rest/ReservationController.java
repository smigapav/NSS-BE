package cz.cvut.fel.ear.reservation_system.rest;

import cz.cvut.fel.ear.reservation_system.dto.ReservationDto;
import cz.cvut.fel.ear.reservation_system.model.*;
import cz.cvut.fel.ear.reservation_system.rest.util.RestUtils;
import cz.cvut.fel.ear.reservation_system.security.model.UserDetails;
import cz.cvut.fel.ear.reservation_system.service.OrderService;
import cz.cvut.fel.ear.reservation_system.service.ReservationService;
import cz.cvut.fel.ear.reservation_system.service.RoomService;
import cz.cvut.fel.ear.reservation_system.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/rest/reservations")
@PreAuthorize("hasAnyAuthority('ADMIN', 'STANDARD_USER')")
public class ReservationController {

    private static final Logger LOG = LoggerFactory.getLogger(ReservationController.class);

    private final ReservationService reservationService;

    private final RoomService roomService;
    private final UserService userService;

    private final OrderService orderService;

    @Autowired
    public ReservationController(ReservationService reservationService, RoomService roomService, UserService userService, OrderService orderService) {
        this.reservationService = reservationService;
        this.roomService = roomService;
        this.userService = userService;
        this.orderService = orderService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, value = "create")
    public ResponseEntity<String> createReservation(Authentication authentication, @RequestBody(required = false) Reservation reservation) {
        if (roomService.isAvailable(reservation.getDateFrom(), reservation.getDateTo(), reservation.getRoom())) {
            String currentUsername = authentication.getName();
            User currentUser = userService.findByUsername(currentUsername);

            reservation.setUser(currentUser);
            reservation.setCreatedAt(LocalDateTime.now());
            reservation.setStatus(ReservationStatus.NOT_PAID);

            reservationService.persist(reservation);
            LOG.debug("Created reservation {}.", reservation);
            final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/{id}", reservation.getId());
            return new ResponseEntity<>(headers, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("The room is not available for the specified dates.", HttpStatus.CONFLICT);
        }
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, value = "edit")
    public ResponseEntity<String> editReservation(Authentication authentication, @RequestBody(required = false) Reservation reservation) {
        Reservation existingReservation = reservationService.find(reservation.getId());

        if (existingReservation == null) {
            return new ResponseEntity<>("Reservation not found.", HttpStatus.NOT_FOUND);
        }

        String currentUsername = authentication.getName();
        User currentUser = userService.findByUsername(currentUsername);

        if (
                (!currentUser.getUsername().equals(existingReservation.getUser().getUsername()) ||
                        !existingReservation.getStatus().equals(ReservationStatus.NOT_PAID)) && !existingReservation.getUser().isAdmin()) {
                            return new ResponseEntity<>("You don't have permission to edit this reservation.", HttpStatus.FORBIDDEN);
                        }

        if (currentUser.getRole().equals(Role.STANDARD_USER)) {
            existingReservation.setDateFrom(reservation.getDateFrom());
            existingReservation.setDateTo(reservation.getDateTo());

            if (roomService.isAvailable(existingReservation.getDateFrom(), existingReservation.getDateTo(), existingReservation.getRoom())) {
                reservationService.update(existingReservation);
                LOG.debug("Edited reservation {}.", existingReservation);
                return new ResponseEntity<>("Reservation successfully edited.", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("The updated reservation conflicts with existing reservations.", HttpStatus.CONFLICT);
            }
        }

        if (existingReservation.getUser().isAdmin()) {
            if (reservation.getRoom() != null) {
                existingReservation.setRoom(reservation.getRoom());
            }

            if (reservation.getDateFrom() != null) {
                existingReservation.setDateFrom(reservation.getDateFrom());
            }

            if (reservation.getDateTo() != null) {
                existingReservation.setDateTo(reservation.getDateTo());
            }

            if (reservation.getCreatedAt() != null) {
                existingReservation.setCreatedAt(reservation.getCreatedAt());
            }

            reservationService.update(existingReservation);
            LOG.debug("Edited reservation {} by admin.", existingReservation);
            return new ResponseEntity<>("Reservation successfully edited by admin.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("You don't have permission to edit this reservation.", HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, value = "storno")
    public ResponseEntity<String> stornoReservation(Principal principal, @RequestBody(required = false) Reservation reservation) {
        Reservation existingReservation = reservationService.find(reservation.getId());

        if (existingReservation == null) {
            return new ResponseEntity<>("Reservation not found.", HttpStatus.NOT_FOUND);
        }

        if (!principal.getName().equals(existingReservation.getUser().getUsername()) && !existingReservation.getUser().isAdmin()) {
            return new ResponseEntity<>("You don't have permission to edit this reservation.", HttpStatus.FORBIDDEN);
        }

        if (existingReservation.getStatus().equals(ReservationStatus.NOT_PAID)) {
            existingReservation.setStatus(ReservationStatus.CANCELLED);
            reservationService.update(existingReservation);
            return new ResponseEntity<>("Reservation has been cancelled successfully.", HttpStatus.OK);

        } else if (existingReservation.getStatus().equals(ReservationStatus.PAID)) {
            existingReservation.setStatus(ReservationStatus.STORNO_REQUEST);
            reservationService.update(existingReservation);
            return new ResponseEntity<>("Your storno request has been submitted, proceed with payment.", HttpStatus.OK);

        } else {
            return new ResponseEntity<>("You cannot cancel this reservation.", HttpStatus.BAD_REQUEST);

        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, value = "pay")
    public ResponseEntity<String> payReservation(Principal principal, @RequestBody(required = false) Reservation reservation) {
        Reservation existingReservation = reservationService.find(reservation.getId());

        if (existingReservation == null) {
            return new ResponseEntity<>("Reservation not found.", HttpStatus.NOT_FOUND);
        }

        if (!principal.getName().equals(existingReservation.getUser().getUsername()) && !existingReservation.getUser().isAdmin()) {
            return new ResponseEntity<>("You don't have the permissions to pay this reservation.", HttpStatus.FORBIDDEN);
        }

        Order order = orderService.findByReservation(existingReservation);


        if (existingReservation.getStatus().equals(ReservationStatus.NOT_PAID)) {
            long hours = Duration.between(existingReservation.getDateFrom(), existingReservation.getDateTo()).toHours();
            order.setTotalPrice(existingReservation.getRoom().getHourlyRate() * hours);
            order.setConfirmedAt(LocalDateTime.now());

            orderService.update(order);

            existingReservation.setStatus(ReservationStatus.PAID);
            reservationService.update(existingReservation);
            return new ResponseEntity<>("Reservation has been paid successfully.", HttpStatus.OK);

        } else if (existingReservation.getStatus().equals(ReservationStatus.STORNO_REQUEST)) {
            order.setTotalPrice(existingReservation.getRoom().getStornoFee());
            order.setConfirmedAt(LocalDateTime.now());

            orderService.update(order);

            existingReservation.setStatus(ReservationStatus.CANCELLED);
            reservationService.update(existingReservation);
            return new ResponseEntity<>("Reservation has been cancelled successfully.", HttpStatus.OK);

        } else {
            return new ResponseEntity<>("You cannot pay this reservation.", HttpStatus.BAD_REQUEST);
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

