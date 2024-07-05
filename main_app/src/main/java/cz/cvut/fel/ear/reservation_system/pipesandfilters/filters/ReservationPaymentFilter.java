package cz.cvut.fel.ear.reservation_system.pipesandfilters.filters;

import cz.cvut.fel.ear.reservation_system.dto.ReservationDTO;
import cz.cvut.fel.ear.reservation_system.exception.PaymentNotAllowedException;
import cz.cvut.fel.ear.reservation_system.mapping.ReservationMapper;
import cz.cvut.fel.ear.reservation_system.model.Order;
import cz.cvut.fel.ear.reservation_system.model.Reservation;
import cz.cvut.fel.ear.reservation_system.model.ReservationStatus;
import cz.cvut.fel.ear.reservation_system.pipesandfilters.Filter;
import cz.cvut.fel.ear.reservation_system.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class ReservationPaymentFilter implements Filter<ReservationDTO> {

    private final OrderService orderService;

    @Autowired
    public ReservationPaymentFilter(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public ReservationDTO execute(ReservationDTO reservationDTO) {
        Reservation reservation = ReservationMapper.INSTANCE.dtoToReservation(reservationDTO);
        Order order = orderService.findByReservation(reservation);

        switch (reservation.getStatus()) {
            case NOT_PAID:
                long hours = Duration.between(reservation.getDateFrom(), reservation.getDateTo()).toHours();
                order.setTotalPrice(reservation.getRoom().getHourlyRate() * hours);
                order.setConfirmedAt(LocalDateTime.now());
                reservation.setStatus(ReservationStatus.PAID);
                break;
            case STORNO_REQUEST:
                order.setTotalPrice(reservation.getRoom().getStornoFee());
                order.setConfirmedAt(LocalDateTime.now());
                reservation.setStatus(ReservationStatus.CANCELLED);
                break;
            default:
                throw new PaymentNotAllowedException(HttpStatus.BAD_REQUEST, "You cannot pay this reservation.");
        }

        return ReservationMapper.INSTANCE.reservationToDto(reservation);
    }
}