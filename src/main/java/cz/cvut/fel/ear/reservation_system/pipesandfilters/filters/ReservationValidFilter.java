package cz.cvut.fel.ear.reservation_system.pipesandfilters.filters;

import cz.cvut.fel.ear.reservation_system.dto.ReservationDTO;
import cz.cvut.fel.ear.reservation_system.pipesandfilters.Filter;

public class ReservationValidFilter implements Filter<ReservationDTO> {
    @Override
    public ReservationDTO execute(ReservationDTO input) {
        if (input.getDateFrom().isAfter(input.getDateTo())) {
            throw new IllegalArgumentException("Invalid reservation dates");
        }
        return input;
    }
}
