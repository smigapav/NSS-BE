package cz.cvut.fel.ear.reservation_system.pipesandfilters.filters;

import cz.cvut.fel.ear.reservation_system.dto.ReservationDTO;
import cz.cvut.fel.ear.reservation_system.model.ReservationStatus;
import cz.cvut.fel.ear.reservation_system.pipesandfilters.Filter;

import java.time.LocalDateTime;

public class ReservationTransformationFilter implements Filter<ReservationDTO> {
    /**
     * Executes the filter operation.
     * This method sets the status of the reservation to NOT_PAID and the creation time to the current time.
     *
     * @param input the reservation data to transform
     * @return the transformed reservation data
     */
    @Override
    public ReservationDTO execute(ReservationDTO input) {
        input.setStatus(ReservationStatus.NOT_PAID);
        input.setCreatedAt(LocalDateTime.now());
        return input;
    }
}
