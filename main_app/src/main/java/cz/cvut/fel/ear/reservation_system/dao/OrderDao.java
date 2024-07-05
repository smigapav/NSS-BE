package cz.cvut.fel.ear.reservation_system.dao;

import cz.cvut.fel.ear.reservation_system.model.Order;
import cz.cvut.fel.ear.reservation_system.model.Reservation;
import cz.cvut.fel.ear.reservation_system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderDao extends JpaRepository<Order, Integer> {
    Optional<Order> findByReservation(Reservation reservation);

    @Query("SELECT o FROM Order o WHERE o.reservation.id = :reservationId")
    Optional<Order> findByReservationId(@Param("reservationId") Integer reservationId);

    List<Order> findAllOrdersByUser(User user);
}
