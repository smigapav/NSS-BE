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

    @Override
    public ReservationDTO execute(ReservationDTO input) {
        if (ReservationService.read(input.getId()) == null) {
            throw new ReservationNotFoundException(HttpStatus.NOT_FOUND, "Reservation not found.");
        }
        return input;
    }
}
