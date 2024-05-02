package cz.cvut.fel.ear.reservation_system.service;

import cz.cvut.fel.ear.reservation_system.dao.ReservationDao;
import cz.cvut.fel.ear.reservation_system.dao.RoomDao;
import cz.cvut.fel.ear.reservation_system.dao.UserDao;
import cz.cvut.fel.ear.reservation_system.model.Room;
import cz.cvut.fel.ear.reservation_system.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Transactional
@AutoConfigureTestEntityManager
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private RoomService sut;

    private Room room;

    @BeforeEach
    public void setUp() {
        room = em.find(Room.class, 5);
    }

    @Test
    public void findByNameReturnsCorrectly()
    {
        Room resultRoom = sut.findByName(room.getName());

        assertEquals(room.getId(), resultRoom.getId());
    }

    @Test
    public void getAllRoomsReturnsSomeRooms()
    {
        List<Room> rooms = sut.getAllRooms();

        assertFalse(rooms.isEmpty());
    }

    @Test
    public void isAvailableReturnsFalse()
    {
        LocalDateTime from = LocalDateTime.of(2015,
                Month.JULY, 29, 19, 30, 40);
        LocalDateTime to = LocalDateTime.now();

        
        assertFalse(sut.isAvailable(from, to, room));
    }

    @Test
    public void findAvailableRoomsReturnsSomeRooms()
    {
        LocalDateTime from = LocalDateTime.of(2015,
                Month.JULY, 29, 19, 30, 40);
        LocalDateTime to = LocalDateTime.now();

        assertFalse(sut.findAvailableRooms(from, to).isEmpty());
    }

}
