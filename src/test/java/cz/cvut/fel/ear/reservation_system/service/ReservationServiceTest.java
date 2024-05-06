package cz.cvut.fel.ear.reservation_system.service;

import cz.cvut.fel.ear.reservation_system.model.Reservation;
import cz.cvut.fel.ear.reservation_system.model.User;
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

import java.util.List;

@SpringBootTest
@Transactional
@AutoConfigureTestEntityManager
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
class ReservationServiceTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ReservationService sut;
    private User user;
    private Reservation reservation;

    ReservationServiceTest() {
    }

    @BeforeEach
    void setUp() {
        reservation = em.find(Reservation.class,3);
        user = em.find(User.class,1);

    }

    @Test
    public void findByUserTest(){
        List<Reservation> reservationList = sut.findByUser(user);
        boolean statement = reservationList.contains(reservation);
        Assertions.assertFalse(statement);
        }

    @Test
    public void findTest(){
        int id = 3;
        Reservation reservation1 = sut.read(id);
        Assertions.assertEquals(reservation,reservation1);
    }
}