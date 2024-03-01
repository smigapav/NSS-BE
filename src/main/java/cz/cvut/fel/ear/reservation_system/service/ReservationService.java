package cz.cvut.fel.ear.reservation_system.service;

import cz.cvut.fel.ear.reservation_system.dao.ReservationDao;
import cz.cvut.fel.ear.reservation_system.model.Reservation;
import cz.cvut.fel.ear.reservation_system.model.ReservationStatus;
import cz.cvut.fel.ear.reservation_system.model.Room;
import cz.cvut.fel.ear.reservation_system.model.User;
import cz.cvut.fel.ear.reservation_system.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class ReservationService {
    private final ReservationDao reservationDao;
    @Autowired
    public ReservationService(ReservationDao reservationDao) {
        this.reservationDao = reservationDao;
    }

    @Transactional
    public void persist(Reservation reservation){
        Objects.requireNonNull(reservation);
        if (reservation.getStatus() == null) {
            reservation.setStatus(Constants.DEFAULT_STATUS);
        }
        reservationDao.persist(reservation);
    }
    @Transactional
    public void delete(Integer id){
        Reservation reservation = reservationDao.find(id);
        if(reservation != null){
            reservationDao.remove(reservation);
        }
    }

    @Transactional
    public void update(Reservation reservation) {
        reservationDao.update(reservation);
    }

    @Transactional(readOnly = true)
    public Reservation find(int id){
        return reservationDao.find(id);
    }

    @Transactional(readOnly = true)
    public List<Reservation> findAll(){
        return reservationDao.findAll();
    }

    @Transactional(readOnly = true)
    public List<Reservation> findByStatus(ReservationStatus status){
        return reservationDao.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Reservation> findByRoom(Room room){
        return reservationDao.findByRoom(room);
    }

    @Transactional(readOnly = true)
    public List<Reservation> findByUser(User user){
        return reservationDao.findByUser(user);
    }

}


