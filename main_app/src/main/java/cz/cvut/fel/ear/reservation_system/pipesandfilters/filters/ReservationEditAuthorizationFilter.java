package cz.cvut.fel.ear.reservation_system.pipesandfilters.filters;

import cz.cvut.fel.ear.reservation_system.dao.ReservationDao;
import cz.cvut.fel.ear.reservation_system.dto.ReservationDTO;
import cz.cvut.fel.ear.reservation_system.exception.ReservationConflictException;
import cz.cvut.fel.ear.reservation_system.mapping.ReservationMapper;
import cz.cvut.fel.ear.reservation_system.model.Reservation;
import cz.cvut.fel.ear.reservation_system.model.Role;
import cz.cvut.fel.ear.reservation_system.model.User;
import cz.cvut.fel.ear.reservation_system.pipesandfilters.Filter;
import cz.cvut.fel.ear.reservation_system.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class ReservationEditAuthorizationFilter implements Filter<ReservationDTO> {

    private final RoomService roomService;
    private final ReservationDao reservationDao;
    private User currentUser;

    @Autowired
    public ReservationEditAuthorizationFilter(RoomService roomService, ReservationDao reservationDao) {
        this.roomService = roomService;
        this.reservationDao = reservationDao;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    @Override
    public ReservationDTO execute(ReservationDTO reservationDTO) {
        Reservation existingReservation = reservationDao.findById(reservationDTO.getId()).orElseThrow();
        Reservation reservation = ReservationMapper.INSTANCE.dtoToReservation(reservationDTO);

        if (currentUser.getRole().equals(Role.STANDARD_USER)) {
            existingReservation.setDateFrom(reservation.getDateFrom());
            existingReservation.setDateTo(reservation.getDateTo());

            if (!roomService.isAvailable(existingReservation.getDateFrom(), existingReservation.getDateTo(), existingReservation.getRoom())) {
                throw new ReservationConflictException(HttpStatus.CONFLICT, "The updated reservation conflicts with existing reservations.");
            }
        }

        if (currentUser.isAdmin()) {
            if (!roomService.isAvailable(reservation.getDateFrom(), reservation.getDateTo(), reservation.getRoom())) {
                throw new ReservationConflictException(HttpStatus.CONFLICT, "The updated reservation conflicts with existing reservations.");
            }
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

        return ReservationMapper.INSTANCE.reservationToDto(existingReservation);
    }
}