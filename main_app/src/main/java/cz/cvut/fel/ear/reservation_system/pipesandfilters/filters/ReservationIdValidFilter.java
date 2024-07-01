package cz.cvut.fel.ear.reservation_system.pipesandfilters.filters;

import cz.cvut.fel.ear.reservation_system.dto.ReservationDTO;
import cz.cvut.fel.ear.reservation_system.exception.ReservationNotFoundException;
import cz.cvut.fel.ear.reservation_system.pipesandfilters.Filter;
import cz.cvut.fel.ear.reservation_system.service.ReservationService;
import org.springframework.http.HttpStatus;

public class ReservationIdValidFilter implements Filter<ReservationDTO> {
    private final ReservationService ReservationService;

    public ReservationIdValidFilter(ReservationService reservationService) {
        ReservationService = reservationService;
    }

    /**
     * Executes the filter operation.
     * This method checks if the reservation with the provided ID exists.
     * If the reservation does not exist, a ReservationNotFoundException is thrown.
     *
     * @param input the reservation data to check
     * @return the checked reservation data
     * @throws ReservationNotFoundException if the reservation does not exist
     */
    @Override
    public ReservationDTO execute(ReservationDTO input) {
        if (ReservationService.read(input.getId()) == null) {
            throw new ReservationNotFoundException(HttpStatus.NOT_FOUND, "Reservation not found.");
        }
        return input;
    }
}
