package cz.cvut.fel.ear.reservation_system.service;

import cz.cvut.fel.ear.reservation_system.dao.ReservationDao;
import cz.cvut.fel.ear.reservation_system.dto.ReservationDTO;
import cz.cvut.fel.ear.reservation_system.exception.CancellationNotAllowedException;
import cz.cvut.fel.ear.reservation_system.exception.PaymentNotAllowedException;
import cz.cvut.fel.ear.reservation_system.exception.ReservationConflictException;
import cz.cvut.fel.ear.reservation_system.exception.RoomNotAvailableException;
import cz.cvut.fel.ear.reservation_system.mapping.ReservationMapper;
import cz.cvut.fel.ear.reservation_system.model.*;
import cz.cvut.fel.ear.reservation_system.pipesandfilters.Pipeline;
import cz.cvut.fel.ear.reservation_system.pipesandfilters.filters.*;
import cz.cvut.fel.ear.reservation_system.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Service class for managing reservations.
 * This class provides CRUD operations for reservations and methods to find reservations by status, room, and user.
 */
@Service
@RequiredArgsConstructor
public class ReservationService implements CRUDOperations<Reservation> {

    /**
     * DAO for accessing reservation data.
     */
    private final ReservationDao reservationDao;

    /**
     * Service for managing rooms.
     */
    private final RoomService roomService;

    /**
     * Service for managing orders.
     */
    private final OrderService orderService;

    /**
     * Creates a new reservation.
     *
     * @param reservation the reservation to create
     * @throws NullPointerException if the provided reservation is null
     */
    @Transactional
    @Override
    public void create(Reservation reservation) {
        Objects.requireNonNull(reservation);
        if (reservation.getStatus() == null) {
            reservation.setStatus(Constants.DEFAULT_STATUS);
        }
        reservationDao.save(reservation);
    }

    /**
     * Deletes a reservation by its ID.
     *
     * @param id the ID of the reservation to delete
     */
    @Transactional
    @Override
    public void delete(Integer id) {
        Optional<Reservation> reservation = reservationDao.findById(id);
        reservation.ifPresent(reservationDao::delete);
    }

    /**
     * Updates an existing reservation.
     *
     * @param reservation the reservation to update
     */
    @Transactional
    @Override
    public void update(Reservation reservation) {
        reservationDao.save(reservation);
    }

    /**
     * Reads a reservation by its ID.
     *
     * @param id the ID of the reservation to read
     * @return the reservation with the provided ID, or null if no such reservation exists
     */
    @Transactional(readOnly = true)
    @Override
    public Reservation read(Integer id) {
        return reservationDao.findById(id).orElse(null);
    }

    /**
     * Lists all reservations.
     *
     * @return a list of all reservations
     */
    @Transactional(readOnly = true)
    @Override
    public List<Reservation> listAll() {
        return reservationDao.findAll();
    }

    /**
     * Finds reservations by their status.
     *
     * @param status the status of the reservations to find
     * @return a list of reservations with the provided status
     */
    @Transactional(readOnly = true)
    public List<Reservation> findByStatus(ReservationStatus status) {
        return reservationDao.findByStatus(status);
    }

    /**
     * Finds reservations by their room.
     *
     * @param room the room of the reservations to find
     * @return a list of reservations with the provided room
     */
    @Transactional(readOnly = true)
    public List<Reservation> findByRoom(Room room) {
        return reservationDao.findByRoom(room);
    }

    /**
     * Finds reservations by their user.
     *
     * @param user the user of the reservations to find
     * @return a list of reservations with the provided user
     */
    @Transactional(readOnly = true)
    public List<Reservation> findByUser(User user) {
        return reservationDao.findByUser(user);
    }

    /**
     * Creates a reservation if the room is available.
     *
     * @param reservationDTO the reservation data to create
     * @return the created reservation, or null if the room is not available
     */
    @Transactional
    public ReservationDTO createReservationIfRoomAvailable(ReservationDTO reservationDTO) {
        Pipeline<ReservationDTO> pipeline = new Pipeline<>();
        pipeline.addFilter(new GenericLoggingFilter<>("creating reservation if room is available", ReservationService.class.getName(), "createReservationIfRoomAvailable"));
        pipeline.addFilter(new ReservationValidFilter());
        pipeline.addFilter(new ReservationTransformationFilter());

        ReservationDTO processedReservationDTO = pipeline.execute(reservationDTO);

        Reservation reservation = ReservationMapper.INSTANCE.INSTANCE.dtoToReservation(processedReservationDTO);
        Room room = reservation.getRoom();
        LocalDateTime from = reservation.getDateFrom();
        LocalDateTime to = reservation.getDateTo();

        if (!roomService.isAvailable(from, to, room)) {
            throw new RoomNotAvailableException(HttpStatus.CONFLICT, "The room is not available for the specified dates.");
        }

        create(reservation);

        return ReservationMapper.INSTANCE.reservationToDto(reservation);
    }

    /**
     * Edits a reservation if possible.
     *
     * @param currentUser    the current user
     * @param reservationDTO the reservation data to edit
     * @return the edited reservation, or null if the reservation could not be edited
     */
    @Transactional
    public ReservationDTO editReservationIfPossible(User currentUser, ReservationDTO reservationDTO) {
        Pipeline<ReservationDTO> pipeline = new Pipeline<>();
        ReservationEditAuthorizationFilter editAuthorizationFilter = new ReservationEditAuthorizationFilter(roomService, reservationDao);
        editAuthorizationFilter.setCurrentUser(currentUser);

        pipeline.addFilter(new GenericLoggingFilter<>("editing reservation if is it possible", ReservationService.class.getName(), "editReservationIfPossible"));
        pipeline.addFilter(new ReservationIdValidFilter(this));
        pipeline.addFilter(editAuthorizationFilter);

        ReservationDTO processedReservationDTO = pipeline.execute(reservationDTO);

        update(ReservationMapper.INSTANCE.dtoToReservation(processedReservationDTO));

        return processedReservationDTO;
    }

    /**
     * Attempts to cancel a reservation if possible based on the current user and reservation details.
     * This method orchestrates a pipeline of filters to validate the reservation ID, check user permissions,
     * and apply cancellation logic. The cancellation logic is encapsulated within the {@link ReservationStornoFilter}.
     *
     * @param currentUser    The current user attempting to cancel the reservation.
     * @param reservationDTO The reservation data transfer object containing details of the reservation to be cancelled.
     * @return The updated {@link ReservationDTO} after attempting cancellation.
     * @throws CancellationNotAllowedException If the cancellation is not allowed based on the reservation status or user permissions.
     */
    @Transactional
    public ReservationDTO stornoReservationIfPossible(User currentUser, ReservationDTO reservationDTO) {
        Pipeline<ReservationDTO> pipeline = new Pipeline<>();
        pipeline.addFilter(new GenericLoggingFilter<>("checking reservation id and permission", ReservationService.class.getName(), "stornoReservationIfPossible"));
        pipeline.addFilter(new ReservationIdValidFilter(this));
        pipeline.addFilter(new ReservationPermissionValidFilter(currentUser, read(reservationDTO.getId())));
        pipeline.addFilter(new ReservationStornoFilter(reservationDao));

        return pipeline.execute(reservationDTO);
    }

    /**
     * Attempts to process payment for a reservation if possible based on the current user and reservation details.
     * This method uses a pipeline of filters to validate the reservation ID, check user permissions, process the payment,
     * and update the reservation status. Payment processing and reservation update logic are encapsulated within
     * the {@link ReservationPaymentFilter} and {@link ReservationUpdateFilter} respectively.
     *
     * @param currentUser    The current user attempting to pay for the reservation.
     * @param reservationDTO The reservation data transfer object containing details of the reservation for which payment is being made.
     * @return The updated {@link ReservationDTO} after attempting payment.
     * @throws PaymentNotAllowedException If the payment is not allowed based on the reservation status or user permissions.
     */
    @Transactional
    public ReservationDTO payReservationIfPossible(User currentUser, ReservationDTO reservationDTO) {
        Pipeline<ReservationDTO> pipeline = new Pipeline<>();
        pipeline.addFilter(new GenericLoggingFilter<>("checking reservation id and permission", ReservationService.class.getName(), "payReservationIfPossible"));
        pipeline.addFilter(new ReservationIdValidFilter(this));
        pipeline.addFilter(new ReservationPermissionValidFilter(currentUser, read(reservationDTO.getId())));
        pipeline.addFilter(new ReservationPaymentFilter(orderService));
        pipeline.addFilter(new ReservationUpdateFilter(reservationDao, orderService));

        return pipeline.execute(reservationDTO);
    }

    /**
     * Generates a response body for the storno (cancellation) endpoint based on the updated reservation status.
     * This method provides a user-friendly message indicating the result of a cancellation request.
     *
     * @param updatedReservation The reservation after attempting cancellation.
     * @return A {@link String} message indicating the outcome of the cancellation request.
     */
    public String getStornoEndpointBody(Reservation updatedReservation) {
        if (updatedReservation.getStatus().equals(ReservationStatus.CANCELLED)) {
            return "Reservation has been cancelled successfully.";
        } else if (updatedReservation.getStatus().equals(ReservationStatus.STORNO_REQUEST)) {
            return "Your storno request has been submitted, proceed with payment.";
        }
        return null;
    }

    /**
     * Generates a response body for the payment endpoint based on the updated reservation status.
     * This method provides a user-friendly message indicating the result of a payment request.
     *
     * @param updatedReservation The reservation after attempting payment.
     * @return A {@link String} message indicating the outcome of the payment request.
     */
    public String getPayEndpointBody(Reservation updatedReservation) {
        if (updatedReservation.getStatus().equals(ReservationStatus.CANCELLED)) {
            return "Reservation has been cancelled successfully.";
        } else if (updatedReservation.getStatus().equals(ReservationStatus.PAID)) {
            return "Reservation has been paid successfully.";
        }
        return null;
    }

    /**
     * Validates the reservation ID and checks if the current user has permission to perform actions on the reservation.
     * This method orchestrates a pipeline of filters to ensure the reservation exists and the user has appropriate permissions.
     *
     * @param reservationDTO The reservation data transfer object containing the reservation ID to check.
     * @param currentUser    The current user whose permissions are being verified.
     * @return The validated and permission-checked {@link ReservationDTO}.
     */
    private ReservationDTO checkIdAndPermission(ReservationDTO reservationDTO, User currentUser) {
        Pipeline<ReservationDTO> pipeline = new Pipeline<>();
        pipeline.addFilter(new GenericLoggingFilter<>("checking reservation id and permission", ReservationService.class.getName(), "checkIdAndPermission"));
        pipeline.addFilter(new ReservationIdValidFilter(this));
        pipeline.addFilter(new ReservationPermissionValidFilter(currentUser, read(reservationDTO.getId())));
        return pipeline.execute(reservationDTO);
    }
}
