package cz.cvut.fel.ear.reservation_system.pipesandfilters.filters;

import cz.cvut.fel.ear.reservation_system.dto.ReservationDTO;
import cz.cvut.fel.ear.reservation_system.model.ReservationStatus;
import cz.cvut.fel.ear.reservation_system.pipesandfilters.Filter;

import java.time.LocalDateTime;

public class ReservationTransformationFilter implements Filter<ReservationDTO> {
    @Override
    public ReservationDTO execute(ReservationDTO input) {
        input.setStatus(ReservationStatus.NOT_PAID);
        input.setCreatedAt(LocalDateTime.now());
        return input;
    }
}
