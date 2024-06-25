package cz.cvut.fel.ear.reservation_system.pipesandfilters.filters;

import cz.cvut.fel.ear.reservation_system.dto.ReservationDTO;
import cz.cvut.fel.ear.reservation_system.exception.PermissionDeniedException;
import cz.cvut.fel.ear.reservation_system.mapping.ReservationMapper;
import cz.cvut.fel.ear.reservation_system.model.Reservation;
import cz.cvut.fel.ear.reservation_system.model.ReservationStatus;
import cz.cvut.fel.ear.reservation_system.model.User;
import cz.cvut.fel.ear.reservation_system.pipesandfilters.Filter;
import org.springframework.http.HttpStatus;

public class ReservationEditValidFilter implements Filter<ReservationDTO> {
    private final User currentUser;

    public ReservationEditValidFilter(User currentUser) {
        this.currentUser = currentUser;
    }

    @Override
    public ReservationDTO execute(ReservationDTO input) {
        Reservation reservation = ReservationMapper.INSTANCE.dtoToReservation(input);
        if ((!currentUser.getUsername().equals(reservation.getUser().getUsername()) ||
                !reservation.getStatus().equals(ReservationStatus.NOT_PAID)) && !reservation.getUser().isAdmin()) {
            throw new PermissionDeniedException(HttpStatus.FORBIDDEN, "You don't have permission to edit this reservation.");
        }
        return input;
    }
}
