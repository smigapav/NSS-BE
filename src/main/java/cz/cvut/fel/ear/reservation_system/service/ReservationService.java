package cz.cvut.fel.ear.reservation_system.service;

import cz.cvut.fel.ear.reservation_system.dao.ReservationDao;
import cz.cvut.fel.ear.reservation_system.dto.ReservationDTO;
import cz.cvut.fel.ear.reservation_system.exception.*;
import cz.cvut.fel.ear.reservation_system.mapping.ReservationMapper;
import cz.cvut.fel.ear.reservation_system.model.*;
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

@Service
@RequiredArgsConstructor
public class ReservationService implements CRUDOperations<Reservation> {
    private final ReservationDao reservationDao;
    private final RoomService roomService;
    private final OrderService orderService;

    @Transactional
    @Override
    public void create(Reservation reservation) {
        Objects.requireNonNull(reservation);
        if (reservation.getStatus() == null) {
            reservation.setStatus(Constants.DEFAULT_STATUS);
        }
        reservationDao.save(reservation);
    }

    @Transactional
    @Override
    public void delete(Integer id) {
        Optional<Reservation> reservation = reservationDao.findById(id);
        reservation.ifPresent(reservationDao::delete);
    }

    @Transactional
    @Override
    public void update(Reservation reservation) {
        reservationDao.save(reservation);
    }

    @Transactional(readOnly = true)
    @Override
    public Reservation read(Integer id) {
        return reservationDao.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Reservation> listAll() {
        return reservationDao.findAll();
    }

    @Transactional(readOnly = true)
    public List<Reservation> findByStatus(ReservationStatus status) {
        return reservationDao.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Reservation> findByRoom(Room room) {
        return reservationDao.findByRoom(room);
    }

    @Transactional(readOnly = true)
    public List<Reservation> findByUser(User user) {
        return reservationDao.findByUser(user);
    }

    @Transactional
    public ReservationDTO createReservationIfRoomAvailable(User user, ReservationDTO reservationDTO) {
        Reservation reservation = ReservationMapper.INSTANCE.INSTANCE.dtoToReservation(reservationDTO);
        Room room = reservation.getRoom();
        LocalDateTime from = reservation.getDateFrom();
        LocalDateTime to = reservation.getDateTo();

        if (!roomService.isAvailable(from, to, room)) {
            throw new RoomNotAvailableException(HttpStatus.CONFLICT, "The room is not available for the specified dates.");
        }

        reservation.setUser(user);
        reservation.setCreatedAt(LocalDateTime.now());
        reservation.setStatus(ReservationStatus.NOT_PAID);

        create(reservation);

        return ReservationMapper.INSTANCE.reservationToDto(reservation);
    }

    @Transactional
    public ReservationDTO editReservationIfPossible(User currentUser, ReservationDTO reservationDTO) {
        Reservation reservation = ReservationMapper.INSTANCE.dtoToReservation(reservationDTO);
        Reservation existingReservation = read(reservation.getId());

        if (existingReservation == null) {
            throw new ReservationNotFoundException(HttpStatus.NOT_FOUND, "Reservation not found.");
        }

        if ((!currentUser.getUsername().equals(existingReservation.getUser().getUsername()) ||
                !existingReservation.getStatus().equals(ReservationStatus.NOT_PAID)) && !existingReservation.getUser().isAdmin()) {
            throw new PermissionDeniedException(HttpStatus.FORBIDDEN, "You don't have permission to edit this reservation.");
        }

        if (currentUser.getRole().equals(Role.STANDARD_USER)) {
            existingReservation.setDateFrom(reservation.getDateFrom());
            existingReservation.setDateTo(reservation.getDateTo());

            if (!roomService.isAvailable(existingReservation.getDateFrom(), existingReservation.getDateTo(), existingReservation.getRoom())) {
                throw new ReservationConflictException(HttpStatus.CONFLICT, "The updated reservation conflicts with existing reservations.");
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
        }

        update(existingReservation);

        return ReservationMapper.INSTANCE.reservationToDto(reservation);
    }

    @Transactional
    public ReservationDTO stornoReservationIfPossible(User currentUser, ReservationDTO reservationDTO) {
        Reservation reservation = ReservationMapper.INSTANCE.dtoToReservation(reservationDTO);
        Reservation existingReservation = read(reservation.getId());

        if (existingReservation == null) {
            throw new ReservationNotFoundException(HttpStatus.NOT_FOUND, "Reservation not found.");
        }

        if (!currentUser.getUsername().equals(existingReservation.getUser().getUsername()) && !existingReservation.getUser().isAdmin()) {
            throw new PermissionDeniedException(HttpStatus.FORBIDDEN, "You don't have permission to cancel this reservation.");
        }

        if (existingReservation.getStatus().equals(ReservationStatus.NOT_PAID)) {
            existingReservation.setStatus(ReservationStatus.CANCELLED);
        } else if (existingReservation.getStatus().equals(ReservationStatus.PAID)) {
            existingReservation.setStatus(ReservationStatus.STORNO_REQUEST);
        } else {
            throw new CancellationNotAllowedException(HttpStatus.BAD_REQUEST, "You cannot cancel this reservation.");
        }

        update(existingReservation);

        return ReservationMapper.INSTANCE.reservationToDto(existingReservation);
    }

    @Transactional
    public ReservationDTO payReservationIfPossible(User currentUser, ReservationDTO reservationDTO) {
        Reservation reservation = ReservationMapper.INSTANCE.dtoToReservation(reservationDTO);
        Reservation existingReservation = read(reservation.getId());

        if (existingReservation == null) {
            throw new ReservationNotFoundException(HttpStatus.NOT_FOUND, "Reservation not found.");
        }

        if (!currentUser.getUsername().equals(existingReservation.getUser().getUsername()) && !existingReservation.getUser().isAdmin()) {
            throw new PermissionDeniedException(HttpStatus.FORBIDDEN, "You don't have permission to pay this reservation.");
        }

        Order order = orderService.findByReservation(existingReservation);

        if (existingReservation.getStatus().equals(ReservationStatus.NOT_PAID)) {
            long hours = Duration.between(existingReservation.getDateFrom(), existingReservation.getDateTo()).toHours();
            order.setTotalPrice(existingReservation.getRoom().getHourlyRate() * hours);
            order.setConfirmedAt(LocalDateTime.now());

            orderService.update(order);

            existingReservation.setStatus(ReservationStatus.PAID);
        } else if (existingReservation.getStatus().equals(ReservationStatus.STORNO_REQUEST)) {
            order.setTotalPrice(existingReservation.getRoom().getStornoFee());
            order.setConfirmedAt(LocalDateTime.now());

            orderService.update(order);

            existingReservation.setStatus(ReservationStatus.CANCELLED);
        } else {
            throw new PaymentNotAllowedException(HttpStatus.BAD_REQUEST, "You cannot pay this reservation.");
        }

        update(existingReservation);

        return ReservationMapper.INSTANCE.reservationToDto(existingReservation);
    }

    public String getStornoEndpointBody(Reservation updatedReservation) {
        if (updatedReservation.getStatus().equals(ReservationStatus.CANCELLED)) {
            return "Reservation has been cancelled successfully.";
        } else if (updatedReservation.getStatus().equals(ReservationStatus.STORNO_REQUEST)) {
            return "Your storno request has been submitted, proceed with payment.";
        }
        return null;
    }

    public String getPayEndpointBody(Reservation updatedReservation) {
        if (updatedReservation.getStatus().equals(ReservationStatus.CANCELLED)) {
            return "Reservation has been cancelled successfully.";
        } else if (updatedReservation.getStatus().equals(ReservationStatus.PAID)) {
            return "Reservation has been paid successfully.";
        }
        return null;
    }
}
