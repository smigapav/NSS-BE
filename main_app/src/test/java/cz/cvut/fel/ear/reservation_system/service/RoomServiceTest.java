package cz.cvut.fel.ear.reservation_system.service;

import cz.cvut.fel.ear.reservation_system.model.Room;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureTestEntityManager
public class RoomServiceTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private RoomService sut;

    private Room room;

    @BeforeEach
    public void setUp() {
        room = new Room();
        room.setName("Test room");
        room.setCapacity(10);
        room.setHourlyRate(100.0);
        room.setStornoFee(10.0);
        em.persist(room);
        em.flush();
        // Set other properties of the room
    }

    @Test
    public void findByNameReturnsCorrectly() {
        Room resultRoom = sut.findByName(room.getName());

        assertEquals(room.getId(), resultRoom.getId());
    }

    @Test
    public void getAllRoomsReturnsSomeRooms() {
        List<Room> rooms = sut.listAll();

        assertFalse(rooms.isEmpty());
    }

    @Test
    public void isAvailableReturnsFalse() {
        LocalDateTime from = LocalDateTime.of(2015,
                Month.JULY, 29, 19, 30, 40);
        LocalDateTime to = LocalDateTime.now();


        assertTrue(sut.isAvailable(from, to, room));
    }

    @Test
    public void findAvailableRoomsReturnsSomeRooms() {
        LocalDateTime from = LocalDateTime.of(2015,
                Month.JULY, 29, 19, 30, 40);
        LocalDateTime to = LocalDateTime.now();

        assertFalse(sut.findAvailableRooms(from, to).isEmpty());
    }
}