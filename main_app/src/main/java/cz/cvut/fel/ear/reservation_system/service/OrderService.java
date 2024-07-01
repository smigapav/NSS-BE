package cz.cvut.fel.ear.reservation_system.service;

import cz.cvut.fel.ear.reservation_system.dao.OrderDao;
import cz.cvut.fel.ear.reservation_system.model.Order;
import cz.cvut.fel.ear.reservation_system.model.Reservation;
import cz.cvut.fel.ear.reservation_system.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Service class for managing orders.
 * This class provides CRUD operations for orders.
 */
@Service
@RequiredArgsConstructor
public class OrderService implements CRUDOperations<Order> {

    /**
     * DAO for accessing order data.
     */
    private final OrderDao orderDao;

    /**
     * Creates a new order.
     *
     * @param order the order to create
     * @throws NullPointerException if the provided order is null
     */
    @Override
    @Transactional
    public void create(Order order) {
        Objects.requireNonNull(order);
        orderDao.save(order);
    }

    /**
     * Deletes an order by its ID.
     *
     * @param id the ID of the order to delete
     */
    @Override
    @Transactional
    public void delete(Integer id) {
        Optional<Order> order = orderDao.findById(id);
        order.ifPresent(orderDao::delete);
    }

    /**
     * Updates an existing order.
     *
     * @param order the order to update
     */
    @Override
    @Transactional
    public void update(Order order) {
        orderDao.save(order);
    }

    /**
     * Reads an order by its ID.
     *
     * @param id the ID of the order to read
     * @return the order with the provided ID, or null if no such order exists
     */
    @Override
    @Transactional(readOnly = true)
    public Order read(Integer id) {
        return orderDao.findById(id).orElse(null);
    }

    /**
     * Lists all orders.
     *
     * @return a list of all orders
     */
    @Override
    @Transactional(readOnly = true)
    public List<Order> listAll() {
        return orderDao.findAll();
    }

    /**
     * Finds all orders by a user.
     *
     * @param user the user whose orders to find
     * @return a list of all orders by the provided user
     */
    @Transactional(readOnly = true)
    public List<Order> findByUser(User user) {
        return orderDao.findAllOrdersByUser(user);
    }

    /**
     * Finds an order by a reservation.
     *
     * @param reservation the reservation whose order to find
     * @return the order for the provided reservation, or null if no such order exists
     */
    @Transactional(readOnly = true)
    public Order findByReservation(Reservation reservation) {
        return orderDao.findByReservation(reservation).orElse(null);
    }
}
