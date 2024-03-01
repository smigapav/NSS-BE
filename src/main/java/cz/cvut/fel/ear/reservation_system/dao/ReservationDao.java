package cz.cvut.fel.ear.reservation_system.dao;


import cz.cvut.fel.ear.reservation_system.model.Reservation;
import cz.cvut.fel.ear.reservation_system.model.ReservationStatus;
import cz.cvut.fel.ear.reservation_system.model.Room;
import cz.cvut.fel.ear.reservation_system.model.User;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public class ReservationDao extends BaseDao<Reservation> {

    public ReservationDao() { super(Reservation.class); }

    public List<Reservation> findByUser(User user) {
        try {
            return em.createQuery("SELECT r FROM Reservation r WHERE r.user = :user", Reservation.class)
                    .setParameter("user", user)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Reservation> findByStatus(ReservationStatus status) {
        try {
            return em.createQuery("SELECT r FROM Reservation r WHERE r.status = :status", Reservation.class)
                    .setParameter("status", status)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Reservation> findByRoom(Room room) {
        try {
            return em.createQuery("SELECT r FROM Reservation r WHERE r.room = :room", Reservation.class)
                    .setParameter("room", room)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Reservation> findByRoomAndActive(Room room) {
        try {
            return em.createQuery("SELECT r FROM Reservation r WHERE r.room = :room AND r.status IN (:statuses)", Reservation.class)
                    .setParameter("room", room)
                    .setParameter("statuses", Arrays.asList(ReservationStatus.NOT_PAID, ReservationStatus.PAID))
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

}