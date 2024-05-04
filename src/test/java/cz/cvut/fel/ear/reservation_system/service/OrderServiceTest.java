package cz.cvut.fel.ear.reservation_system.service;

import cz.cvut.fel.ear.reservation_system.dao.OrderDao;
import cz.cvut.fel.ear.reservation_system.dao.ReservationDao;
import cz.cvut.fel.ear.reservation_system.model.Order;
import cz.cvut.fel.ear.reservation_system.model.Reservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class OrderServiceTest {

    @MockBean
    private OrderDao orderDao;

    @MockBean
    private ReservationDao reservationDao;

    @Autowired
    private OrderService sut;
    private Order order;
    private Reservation reservation;

    @BeforeEach
    public void setUp() {

        order = new Order();
        order.setId(3);

        reservation = new Reservation();
        reservation.setId(4);

        Mockito.when(orderDao.findById(order.getId())).thenReturn(Optional.of(order));
        Mockito.when(orderDao.findByReservation(reservation)).thenReturn(Optional.of(order));
    }

    @Test
    public void readReturnsCorrectOrder() {
        Order resultOrder = sut.read(order.getId());

        assertEquals(order, resultOrder);
    }

    @Test
    public void findByReservationReturnsCorrectOrder() {
        Order resultOrder = sut.findByReservation(reservation);

        assertEquals(order, resultOrder);
    }
}