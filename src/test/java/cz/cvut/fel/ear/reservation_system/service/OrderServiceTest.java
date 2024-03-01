package cz.cvut.fel.ear.reservation_system.service;


import cz.cvut.fel.ear.reservation_system.ReservationSystemApplication;
import cz.cvut.fel.ear.reservation_system.model.Order;
import cz.cvut.fel.ear.reservation_system.model.Reservation;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(
        classes = ReservationSystemApplication.class,
        properties = "spring.config.name=application-test"
)@Transactional
@AutoConfigureTestEntityManager
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
class OrderServiceTest {
    @Autowired
    private TestEntityManager em;

    @Autowired
    private OrderService sut;
    private Reservation reservation;
    private Order order;

    @BeforeEach
    void setUp() {
        reservation = em.find(Reservation.class,4);
        order = em.find(Order.class,3);
    }


    @Test
    public void findTest(){
        int id = 3;
        Order order1 = sut.find(id);
        Assertions.assertEquals(order1, order);
    }

    @Test
    public void findByReservationTest(){
        Order order1 = sut.findByReservation(reservation);
        Assertions.assertEquals(order1,order);
    }
}