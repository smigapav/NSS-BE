package cz.cvut.fel.ear.reservation_system.service;

import cz.cvut.fel.ear.reservation_system.dao.ReservationDao;
import cz.cvut.fel.ear.reservation_system.dao.RoomDao;
import cz.cvut.fel.ear.reservation_system.model.Reservation;
import cz.cvut.fel.ear.reservation_system.model.Room;
import cz.cvut.fel.ear.reservation_system.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class RoomService {

    private final RoomDao dao;
    private final ReservationDao reservationDao;

    public RoomService(RoomDao dao, ReservationDao reservationDao) {
        this.dao = dao;
        this.reservationDao = reservationDao;
    }

    @Transactional
    public void persist(Room room) {
        Objects.requireNonNull(room);

        dao.persist(room);
    }

    @Transactional
    public void delete(Integer id) {
        Room room = dao.find(id);
        if (room != null) {
            dao.remove(room);
        }
    }

    @Transactional
    public void update(Room room) {
        dao.update(room);
    }

    @Transactional(readOnly = true)
    public Room find(Integer id) {
        return dao.find(id);
    }

    @Transactional(readOnly = true)
    public List<Room> getAllRooms() {
        return dao.findAll();
    }

    @Transactional(readOnly = true)
    public Room findByName(String name) {
        return dao.findByName(name);
    }

    @Transactional(readOnly = true)
    public boolean isAvailable(LocalDateTime from, LocalDateTime to, Room room) {
        List<Reservation> res = reservationDao.findByRoomAndActive(room);

        for (Reservation i : res) {
            LocalDateTime existingFrom = i.getDateFrom();
            LocalDateTime existingTo = i.getDateTo();

            if (from.isBefore(existingTo) && to.isAfter(existingFrom)) {
                return false;
            }
        }

        return true;
    }

    @Transactional(readOnly = true)
    public List<Room> findAvailableRooms(LocalDateTime from, LocalDateTime to) {
        List<Room> availableRooms = new ArrayList<>();

        List<Room> allRooms = dao.findAll();

        for (Room room : allRooms) {
            if (isAvailable(from, to, room)) {
                availableRooms.add(room);
            }
        }

        return availableRooms;
    }

}
