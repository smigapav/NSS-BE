package cz.cvut.fel.ear.reservation_system.service;

import cz.cvut.fel.ear.reservation_system.dao.ReservationDao;
import cz.cvut.fel.ear.reservation_system.dao.RoomDao;
import cz.cvut.fel.ear.reservation_system.model.Reservation;
import cz.cvut.fel.ear.reservation_system.model.Room;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class RoomService implements CRUDOperations<Room>{

    private final RoomDao dao;
    private final ReservationDao reservationDao;

    public RoomService(RoomDao dao, ReservationDao reservationDao) {
        this.dao = dao;
        this.reservationDao = reservationDao;
    }

    @Transactional
    @Override
    public void create(Room room) {
        Objects.requireNonNull(room);

        dao.persist(room);
    }

    @Transactional
    @Override
    public void delete(Integer id) {
        Room room = dao.find(id);
        if (room != null) {
            dao.remove(room);
        }
    }

    @Transactional
    @Override
    public void update(Room room) {
        dao.update(room);
    }

    @Transactional(readOnly = true)
    @Override
    public Room read(Integer id) {
        return dao.find(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Room> listAll() {
        return dao.findAll();
    }

    @Transactional(readOnly = true)
    public Room findByName(String name) {
        return dao.findByName(name);
    }

    @Transactional(readOnly = true)
    public boolean isAvailable(LocalDateTime from, LocalDateTime to, Room room) {
        List<Reservation> res = reservationDao.findByRoomAndActive(room);

        return res.stream()
            .noneMatch(i -> {
                LocalDateTime existingFrom = i.getDateFrom();
                LocalDateTime existingTo = i.getDateTo();
                return from.isBefore(existingTo) && to.isAfter(existingFrom);
            });
    }

    @Transactional(readOnly = true)
    public List<Room> findAvailableRooms(LocalDateTime from, LocalDateTime to) {
        List<Room> allRooms = dao.findAll();

        return allRooms.stream()
                .filter(room -> isAvailable(from, to, room))
                .collect(Collectors.toList());
    }
}
