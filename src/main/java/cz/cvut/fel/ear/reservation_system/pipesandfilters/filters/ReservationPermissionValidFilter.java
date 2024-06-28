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

    /**
     * Executes the filter operation.
     * This method checks if the current user has permission to cancel the reservation.
     * If the current user is not the user who made the reservation and the user is not an admin, a PermissionDeniedException is thrown.
     *
     * @param input the reservation data to check
     * @return the checked reservation data
     * @throws PermissionDeniedException if the current user does not have permission to cancel the reservation
     */
    @Override
    public ReservationDTO execute(ReservationDTO input) {
        Reservation existingReservation = ReservationMapper.INSTANCE.dtoToReservation(input);
        if (!currentUser.getUsername().equals(existingReservation.getUser().getUsername()) && !existingReservation.getUser().isAdmin()) {
            throw new PermissionDeniedException(HttpStatus.FORBIDDEN, "You don't have permission to cancel this reservation.");
        }
        return input;
    }
}
