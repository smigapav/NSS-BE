package cz.cvut.fel.ear.reservation_system.pipesandfilters.filters;

import cz.cvut.fel.ear.reservation_system.dao.ReservationDao;
import cz.cvut.fel.ear.reservation_system.dto.ReservationDTO;
import cz.cvut.fel.ear.reservation_system.mapping.ReservationMapper;
import cz.cvut.fel.ear.reservation_system.model.Order;
import cz.cvut.fel.ear.reservation_system.model.Reservation;
import cz.cvut.fel.ear.reservation_system.pipesandfilters.Filter;
import cz.cvut.fel.ear.reservation_system.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReservationUpdateFilter implements Filter<ReservationDTO> {

    private final ReservationDao reservationDao;
    private final OrderService orderService;

    @Autowired
    public ReservationUpdateFilter(ReservationDao reservationDao, OrderService orderService) {
        this.reservationDao = reservationDao;
        this.orderService = orderService;
    }

    @Override
    public ReservationDTO execute(ReservationDTO reservationDTO) {
        Reservation reservation = ReservationMapper.INSTANCE.dtoToReservation(reservationDTO);
        reservationDao.save(reservation);
        Order order = orderService.findByReservation(reservation);
        orderService.update(order);
        return reservationDTO;
    }
}