package cz.cvut.fel.ear.reservation_system.service;

import cz.cvut.fel.ear.reservation_system.dao.OrderDao;
import cz.cvut.fel.ear.reservation_system.model.Order;
import cz.cvut.fel.ear.reservation_system.model.Reservation;
import cz.cvut.fel.ear.reservation_system.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class OrderService implements CRUDOperations<Order> {

    private final OrderDao orderDao;

    public OrderService(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    @Override
    @Transactional
    public void create(Order order){
        Objects.requireNonNull(order);
        orderDao.persist(order);
    }

    @Override
    @Transactional
    public void delete(Integer id){
        Order order = orderDao.find(id);
        if(order != null){
            orderDao.remove(order);
        }
    }

    @Override
    @Transactional
    public void update(Order order) {
        orderDao.update(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Order read(Integer id){
        return orderDao.find(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> listAll(){
        return orderDao.findAll();
    }

    @Transactional(readOnly = true)
    public List<Order> findByUser(User user){
        return orderDao.findAllOrdersByUser(user);
    }

    @Transactional(readOnly = true)
    public Order findByReservation(Reservation reservation){
            return orderDao.findByReservation(reservation);
    }
}