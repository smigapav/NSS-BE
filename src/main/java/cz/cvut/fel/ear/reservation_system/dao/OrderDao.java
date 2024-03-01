package cz.cvut.fel.ear.reservation_system.dao;

import cz.cvut.fel.ear.reservation_system.model.Order;
import cz.cvut.fel.ear.reservation_system.model.Reservation;
import cz.cvut.fel.ear.reservation_system.model.User;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class OrderDao extends BaseDao<Order> {

    public OrderDao() { super(Order.class); }

    public Order findByReservation(Reservation reservation) {
        try {
            return em.createQuery("SELECT o FROM Order o WHERE o.reservation = :reservation", Order.class)
                    .setParameter("reservation", reservation)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Order> findAllOrdersByUser(User user) {
        List<Reservation> reservations = em.createQuery("SELECT r FROM Reservation r WHERE r.user = :user", Reservation.class)
                .setParameter("user", user)
                .getResultList();

        List<Order> orders = new ArrayList<>();
        for (Reservation reservation : reservations) {
            Order order = findByReservation(reservation);
            if (order != null) {
                orders.add(order);
            }
        }

        return orders;
    }
}
