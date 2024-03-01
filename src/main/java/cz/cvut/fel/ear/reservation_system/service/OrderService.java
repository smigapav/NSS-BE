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
public class OrderService {

    private final OrderDao orderDao;

    public OrderService(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    @Transactional
    public void persist(Order order){
        Objects.requireNonNull(order);
        orderDao.persist(order);
    }
    @Transactional
    public void delete(Integer id){
        Order order = orderDao.find(id);
        if(order != null){
            orderDao.remove(order);
        }
    }

    @Transactional
    public void update(Order order) {
        orderDao.update(order);
    }

    @Transactional(readOnly = true)
    public Order find(int id){
        return orderDao.find(id);
    }

    @Transactional(readOnly = true)
    public List<Order> findAll(){
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


