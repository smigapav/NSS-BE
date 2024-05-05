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

@Service
@RequiredArgsConstructor
public class OrderService implements CRUDOperations<Order> {

    private final OrderDao orderDao;

    @Override
    @Transactional
    public void create(Order order) {
        Objects.requireNonNull(order);
        orderDao.save(order);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        Optional<Order> order = orderDao.findById(id);
        order.ifPresent(orderDao::delete);
    }

    @Override
    @Transactional
    public void update(Order order) {
        orderDao.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Order read(Integer id) {
        return orderDao.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> listAll() {
        return orderDao.findAll();
    }

    @Transactional(readOnly = true)
    public List<Order> findByUser(User user) {
        return orderDao.findAllOrdersByUser(user);
    }

    @Transactional(readOnly = true)
    public Order findByReservation(Reservation reservation) {
        return orderDao.findByReservation(reservation).orElse(null);
    }
}
