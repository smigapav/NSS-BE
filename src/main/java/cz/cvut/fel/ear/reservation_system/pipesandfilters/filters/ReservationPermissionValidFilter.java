package cz.cvut.fel.ear.reservation_system.pipesandfilters.filters;

import cz.cvut.fel.ear.reservation_system.dto.ReservationDTO;
import cz.cvut.fel.ear.reservation_system.exception.PermissionDeniedException;
import cz.cvut.fel.ear.reservation_system.mapping.ReservationMapper;
import cz.cvut.fel.ear.reservation_system.model.Reservation;
import cz.cvut.fel.ear.reservation_system.model.User;
import cz.cvut.fel.ear.reservation_system.pipesandfilters.Filter;
import org.springframework.http.HttpStatus;

public class ReservationPermissionValidFilter implements Filter<ReservationDTO> {
    private final User currentUser;

    public ReservationPermissionValidFilter(User currentUser) {
        this.currentUser = currentUser;
    }

    @Override
    public ReservationDTO execute(ReservationDTO input) {
        Reservation existingReservation = ReservationMapper.INSTANCE.dtoToReservation(input);
        if (!currentUser.getUsername().equals(existingReservation.getUser().getUsername()) && !existingReservation.getUser().isAdmin()) {
            throw new PermissionDeniedException(HttpStatus.FORBIDDEN, "You don't have permission to cancel this reservation.");
        }
        return input;
    }
}
