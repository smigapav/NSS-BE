package cz.cvut.fel.ear.reservation_system.rest;

import cz.cvut.fel.ear.reservation_system.dto.ReservationDTO;
import cz.cvut.fel.ear.reservation_system.exception.*;
import cz.cvut.fel.ear.reservation_system.mapping.ReservationMapper;
import cz.cvut.fel.ear.reservation_system.mapping.UserMapper;
import cz.cvut.fel.ear.reservation_system.model.Order;
import cz.cvut.fel.ear.reservation_system.model.Reservation;
import cz.cvut.fel.ear.reservation_system.model.ReservationStatus;
import cz.cvut.fel.ear.reservation_system.model.User;
import cz.cvut.fel.ear.reservation_system.rest.util.RestUtils;
import cz.cvut.fel.ear.reservation_system.security.model.UserDetails;
import cz.cvut.fel.ear.reservation_system.service.CleanUpService;
import cz.cvut.fel.ear.reservation_system.service.OrderService;
import cz.cvut.fel.ear.reservation_system.service.ReservationService;
import cz.cvut.fel.ear.reservation_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/rest/reservations")
@PreAuthorize("hasAnyAuthority('ADMIN', 'STANDARD_USER')")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ReservationController {

    private static final Logger LOG = LoggerFactory.getLogger(ReservationController.class);

    private final ReservationService reservationService;
    private final UserService userService;
    private final OrderService orderService;
    private final CleanUpService cleanupService;


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, value = "create")
    public ResponseEntity<String> createReservation(Authentication authentication, @RequestBody ReservationDTO reservationDTO) {
        try {
            User currentUser = userService.findByUsername(authentication.getName());

            reservationDTO.setUser(UserMapper.INSTANCE.userToDto(currentUser));

            ReservationDTO createdReservationDTO = reservationService.createReservationIfRoomAvailable(reservationDTO);
            Reservation createdReservation = ReservationMapper.INSTANCE.dtoToReservation(createdReservationDTO);

            Order order = new Order.Builder()
                    .withReservation(createdReservation)
                    .withUser(currentUser)
                    .withCreatedAt(LocalDateTime.now())
                    .build();

            orderService.create(order);

            LOG.info("Created reservation {}.", createdReservation);
            final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/{id}", createdReservation.getId());
            return new ResponseEntity<>(headers, HttpStatus.CREATED);
        } catch (RoomNotAvailableException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, value = "edit")
    public ResponseEntity<String> editReservation(Authentication authentication, @RequestBody ReservationDTO reservationDTO) {
        try {
            User currentUser = userService.findByUsername(authentication.getName());

            Reservation reservation = ReservationMapper.INSTANCE.dtoToReservation(reservationDTO);
            reservationService.editReservationIfPossible(currentUser, reservationDTO);

            LOG.info("Edited reservation {}.", reservation);
            return new ResponseEntity<>("Reservation successfully edited.", HttpStatus.OK);
        } catch (ReservationNotFoundException | PermissionDeniedException | ReservationConflictException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, value = "storno")
    public ResponseEntity<String> stornoReservation(Principal principal, @RequestBody ReservationDTO reservationDTO) {
        try {
            String currentUsername = principal.getName();
            User currentUser = userService.findByUsername(currentUsername);

            Reservation reservation = reservationService.read(reservationDTO.getId());
            reservationService.stornoReservationIfPossible(currentUser, reservationDTO);

            LOG.info("Cancelled reservation {}.", reservation);
            return new ResponseEntity<>(reservationService.getStornoEndpointBody(reservation), HttpStatus.OK);
        } catch (ReservationNotFoundException | PermissionDeniedException | IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, value = "pay")
    public ResponseEntity<String> payReservation(Principal principal, @RequestBody ReservationDTO reservationDTO) {
        try {
            String currentUsername = principal.getName();
            User currentUser = userService.findByUsername(currentUsername);

            Reservation reservation = reservationService.read(reservationDTO.getId());
            reservationService.payReservationIfPossible(currentUser, ReservationMapper.INSTANCE.reservationToDto(reservation));

            LOG.info("Paid reservation {}.", reservation);
            return new ResponseEntity<>(reservationService.getPayEndpointBody(reservation), HttpStatus.OK);
        } catch (ReservationNotFoundException | PermissionDeniedException | IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ReservationDTO> listAllReservations() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ADMIN"));

        List<Reservation> reservations;

        if (isAdmin) {
            reservations = Stream.of(
                            reservationService.findByStatus(ReservationStatus.NOT_PAID),
                            reservationService.findByStatus(ReservationStatus.PAID),
                            reservationService.findByStatus(ReservationStatus.STORNO_REQUEST))
                    .flatMap(Collection::stream)
                    .toList();
        } else {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            User currentUser = userService.findByUsername(username);
            reservations = reservationService.findByUser(currentUser);
        }

        LOG.info("Listed reservations.");

        return reservations.stream()
                .map(ReservationMapper.INSTANCE::reservationToDto)
                .collect(Collectors.toList());
    }
}

