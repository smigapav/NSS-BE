package cz.cvut.fel.ear.reservation_system.dao;

import cz.cvut.fel.ear.reservation_system.model.User;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao extends BaseDao<User> {

    public UserDao() { super(User.class); }

    public User findByEmail(String email) {
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    public User findByPhone(String phone) {
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.phone = :phone", User.class)
                    .setParameter("phone", phone)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public User findByUsername(String username) {
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}

