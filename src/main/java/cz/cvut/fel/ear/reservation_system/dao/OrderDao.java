package cz.cvut.fel.ear.reservation_system.dao;

import cz.cvut.fel.ear.reservation_system.model.Order;
import cz.cvut.fel.ear.reservation_system.model.Reservation;
import cz.cvut.fel.ear.reservation_system.model.User;
import jakarta.persistence.NoResultException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public interface OrderDao extends JpaRepository<Order, Integer> {
    Optional<Order> findByReservation(Reservation reservation);
    List<Order> findAllOrdersByUser(User user);
}
