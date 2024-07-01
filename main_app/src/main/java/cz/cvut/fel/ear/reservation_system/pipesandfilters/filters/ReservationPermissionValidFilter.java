package cz.cvut.fel.ear.reservation_system.pipesandfilters.filters;

import cz.cvut.fel.ear.reservation_system.dto.ReservationDTO;
import cz.cvut.fel.ear.reservation_system.exception.PermissionDeniedException;
import cz.cvut.fel.ear.reservation_system.model.Reservation;
import cz.cvut.fel.ear.reservation_system.model.User;
import cz.cvut.fel.ear.reservation_system.pipesandfilters.Filter;
import org.springframework.http.HttpStatus;

public class ReservationPermissionValidFilter implements Filter<ReservationDTO> {
    private final User currentUser;
    private final Reservation currentReservation;

    public ReservationPermissionValidFilter(User currentUser, Reservation reservation) {
        this.currentUser = currentUser;
        this.currentReservation = reservation;
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
        if (!currentUser.getUsername().equals(currentReservation.getUser().getUsername()) && !currentReservation.getUser().isAdmin()) {
            throw new PermissionDeniedException(HttpStatus.FORBIDDEN, "You don't have permission to cancel this reservation.");
        }
        return input;
    }
}
