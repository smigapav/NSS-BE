package cz.cvut.fel.ear.reservation_system.pipesandfilters.filters;

import cz.cvut.fel.ear.reservation_system.dao.ReservationDao;
import cz.cvut.fel.ear.reservation_system.model.Reservation;
import cz.cvut.fel.ear.reservation_system.model.ReservationStatus;
import cz.cvut.fel.ear.reservation_system.model.Room;
import cz.cvut.fel.ear.reservation_system.pipesandfilters.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class RoomAvailabilityFilter implements Filter<Room> {

    private final ReservationDao reservationDao;
    private LocalDateTime from;
    private LocalDateTime to;

    @Autowired
    public RoomAvailabilityFilter(ReservationDao reservationDao) {
        this.reservationDao = reservationDao;
    }

    public void setFrom(LocalDateTime from) {
        this.from = from;
    }

    public void setTo(LocalDateTime to) {
        this.to = to;
    }

    @Override
    public Room execute(Room room) {
        List<Reservation> res = reservationDao.findByRoomAndActive(room,
                List.of(ReservationStatus.PAID, ReservationStatus.NOT_PAID));

        LocalDateTime finalFrom = from != null ? from : LocalDateTime.now();
        LocalDateTime finalTo = to != null ? to : LocalDateTime.now().plusDays(7);

        boolean isAvailable = res.stream().noneMatch(i -> {
            LocalDateTime existingFrom = i.getDateFrom();
            LocalDateTime existingTo = i.getDateTo();
            return finalFrom.isBefore(existingTo) && finalTo.isAfter(existingFrom);
        });

        return isAvailable ? room : null;
    }
}