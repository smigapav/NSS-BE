package cz.cvut.fel.ear.reservation_system.pipesandfilters.filters;

import cz.cvut.fel.ear.reservation_system.dao.ReservationDao;
import cz.cvut.fel.ear.reservation_system.model.Reservation;
import cz.cvut.fel.ear.reservation_system.model.ReservationStatus;
import cz.cvut.fel.ear.reservation_system.pipesandfilters.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DeleteNotPaidReservationsFilter implements Filter<String> {

    private final ReservationDao reservationDao;

    @Autowired
    public DeleteNotPaidReservationsFilter(ReservationDao reservationDao) {
        this.reservationDao = reservationDao;
    }

    @Override
    public String execute(String apiKey) {
        LocalDateTime oneDayFromNow = LocalDateTime.now().plusDays(1);
        List<Reservation> reservations = reservationDao.findByStatusAndDateFromBefore(ReservationStatus.NOT_PAID, oneDayFromNow);
        reservationDao.deleteAll(reservations);
        return "Deleted " + reservations.size() + " not paid reservations less than one day from now.";
    }
}