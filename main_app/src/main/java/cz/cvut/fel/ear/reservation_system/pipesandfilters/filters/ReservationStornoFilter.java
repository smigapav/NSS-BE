package cz.cvut.fel.ear.reservation_system.pipesandfilters.filters;

import cz.cvut.fel.ear.reservation_system.dao.ReservationDao;
import cz.cvut.fel.ear.reservation_system.dto.ReservationDTO;
import cz.cvut.fel.ear.reservation_system.exception.CancellationNotAllowedException;
import cz.cvut.fel.ear.reservation_system.mapping.ReservationMapper;
import cz.cvut.fel.ear.reservation_system.model.Reservation;
import cz.cvut.fel.ear.reservation_system.model.ReservationStatus;
import cz.cvut.fel.ear.reservation_system.pipesandfilters.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class ReservationStornoFilter implements Filter<ReservationDTO> {

    private final ReservationDao reservationDao;

    @Autowired
    public ReservationStornoFilter(ReservationDao reservationDao) {
        this.reservationDao = reservationDao;
    }

    @Override
    public ReservationDTO execute(ReservationDTO reservationDTO) {
        Reservation existingReservation = reservationDao.findById(reservationDTO.getId()).orElseThrow();

        switch (existingReservation.getStatus()) {
            case NOT_PAID:
                existingReservation.setStatus(ReservationStatus.CANCELLED);
                break;
            case PAID:
                existingReservation.setStatus(ReservationStatus.STORNO_REQUEST);
                break;
            default:
                throw new CancellationNotAllowedException(HttpStatus.BAD_REQUEST, "You cannot cancel this reservation.");
        }

        reservationDao.save(existingReservation);
        return ReservationMapper.INSTANCE.reservationToDto(existingReservation);
    }
}