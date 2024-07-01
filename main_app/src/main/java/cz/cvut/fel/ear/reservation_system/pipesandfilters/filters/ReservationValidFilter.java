package cz.cvut.fel.ear.reservation_system.pipesandfilters.filters;

import cz.cvut.fel.ear.reservation_system.dto.ReservationDTO;
import cz.cvut.fel.ear.reservation_system.pipesandfilters.Filter;

public class ReservationValidFilter implements Filter<ReservationDTO> {
    /**
     * Executes the filter operation.
     * This method checks if the reservation dates are valid.
     * If the start date of the reservation is after the end date, an IllegalArgumentException is thrown.
     *
     * @param input the reservation data to check
     * @return the checked reservation data
     * @throws IllegalArgumentException if the start date of the reservation is after the end date
     */
    @Override
    public ReservationDTO execute(ReservationDTO input) {
        if (input.getDateFrom().isAfter(input.getDateTo())) {
            throw new IllegalArgumentException("Invalid reservation dates");
        }
        return input;
    }
}
