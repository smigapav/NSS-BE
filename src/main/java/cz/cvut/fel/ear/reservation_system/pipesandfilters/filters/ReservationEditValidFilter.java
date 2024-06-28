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

    /**
     * Executes the filter operation.
     * This method checks if the current user has permission to edit the reservation.
     * If the current user is not the user who made the reservation, or the reservation status is not NOT_PAID, and the user is not an admin, a PermissionDeniedException is thrown.
     *
     * @param input the reservation data to check
     * @return the checked reservation data
     * @throws PermissionDeniedException if the current user does not have permission to edit the reservation
     */
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
