package cz.cvut.fel.ear.reservation_system.dao;

import cz.cvut.fel.ear.reservation_system.model.Phone;
import cz.cvut.fel.ear.reservation_system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDao extends JpaRepository<User, Integer>{
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(Phone phone);
    Optional<User> findByUsername(String username);
}
