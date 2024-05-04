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
import java.util.Optional;

@Service
public class ReservationService implements CRUDOperations<Reservation>{
    private final ReservationDao reservationDao;
    public ReservationService(ReservationDao reservationDao) {
        this.reservationDao = reservationDao;
    }

    @Transactional
    @Override
    public void create(Reservation reservation){
        Objects.requireNonNull(reservation);
        if (reservation.getStatus() == null) {
            reservation.setStatus(Constants.DEFAULT_STATUS);
        }
        reservationDao.save(reservation);
    }
    @Transactional
    @Override
    public void delete(Integer id){
        Optional<Reservation> reservation = reservationDao.findById(id);
        reservation.ifPresent(reservationDao::delete);
    }

    @Transactional
    @Override
    public void update(Reservation reservation) {
        reservationDao.save(reservation);
    }

    @Transactional(readOnly = true)
    @Override
    public Reservation read(Integer id){
        return reservationDao.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Reservation> listAll(){
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


