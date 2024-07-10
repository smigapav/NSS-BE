package cz.cvut.fel.ear.reservation_system.service;

import cz.cvut.fel.ear.reservation_system.dao.ReservationDao;
import cz.cvut.fel.ear.reservation_system.dao.RoomDao;
import cz.cvut.fel.ear.reservation_system.model.Reservation;
import cz.cvut.fel.ear.reservation_system.model.ReservationStatus;
import cz.cvut.fel.ear.reservation_system.model.Room;
import cz.cvut.fel.ear.reservation_system.pipesandfilters.filters.AvailableRoomsFilter;
import cz.cvut.fel.ear.reservation_system.pipesandfilters.filters.RoomAvailabilityFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing rooms.
 * This class provides CRUD operations for rooms and methods to check room availability.
 */
@Service
@RequiredArgsConstructor
public class RoomService implements CRUDOperations<Room> {

    /**
     * DAO for accessing room data.
     */
    private final RoomDao dao;

    /**
     * DAO for accessing reservation data.
     */
    private final ReservationDao reservationDao;

    /**
     * Creates a new room.
     *
     * @param room the room to create
     * @throws NullPointerException if the provided room is null
     */
    @Transactional
    @Override
    public void create(Room room) {
        Objects.requireNonNull(room);
        dao.save(room);
    }

    /**
     * Deletes a room by its ID.
     *
     * @param id the ID of the room to delete
     */
    @Transactional
    @Override
    public void delete(Integer id) {
        Optional<Room> room = dao.findById(id);
        room.ifPresent(dao::delete);
    }

    /**
     * Updates an existing room.
     *
     * @param room the room to update
     */
    @Transactional
    @Override
    public void update(Room room) {
        dao.save(room);
    }

    /**
     * Reads a room by its ID.
     *
     * @param id the ID of the room to read
     * @return the room with the provided ID, or null if no such room exists
     */
    @Transactional(readOnly = true)
    @Override
    public Room read(Integer id) {
        return dao.findById(id).orElse(null);
    }

    /**
     * Lists all rooms.
     *
     * @return a list of all rooms
     */
    @Transactional(readOnly = true)
    @Override
    public List<Room> listAll() {
        return dao.findAll();
    }

    /**
     * Finds a room by its name.
     *
     * @param name the name of the room to find
     * @return the room with the provided name, or null if no such room exists
     */
    @Transactional(readOnly = true)
    public Room findByName(String name) {
        return dao.findByName(name).orElse(null);
    }

    /**
     * Checks if a room is available between two dates.
     *
     * @param from the start date
     * @param to   the end date
     * @param room the room to check
     * @return true if the room is available, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean isAvailable(LocalDateTime from, LocalDateTime to, Room room) {
        RoomAvailabilityFilter filter = new RoomAvailabilityFilter(reservationDao);
        filter.setFrom(from);
        filter.setTo(to);
        return filter.execute(room) != null;
    }

    /**
     * Finds all rooms that are available between two dates.
     *
     * @param from the start date
     * @param to   the end date
     * @return a list of all available rooms
     */
    @Transactional(readOnly = true)
    public List<Room> findAvailableRooms(LocalDateTime from, LocalDateTime to) {
        List<Room> allRooms = dao.findAll();
        AvailableRoomsFilter filter = new AvailableRoomsFilter(dao, new RoomAvailabilityFilter(reservationDao));
        filter.setDates(from, to);
        return filter.execute(allRooms);
    }
}
