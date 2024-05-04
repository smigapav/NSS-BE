package cz.cvut.fel.ear.reservation_system.service;
import cz.cvut.fel.ear.reservation_system.dao.UserDao;
import cz.cvut.fel.ear.reservation_system.model.Phone;
import cz.cvut.fel.ear.reservation_system.model.Role;
import cz.cvut.fel.ear.reservation_system.model.Room;
import cz.cvut.fel.ear.reservation_system.model.User;
import cz.cvut.fel.ear.reservation_system.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
public class UserService implements CRUDOperations<User> {

    private final UserDao dao;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserDao dao, PasswordEncoder passwordEncoder) {
        this.dao = dao;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @Override
    public void create(User user) {
        Objects.requireNonNull(user);
        user.encodePassword(passwordEncoder);
        if (user.getRole() == null) {
            user.setRole(Constants.DEFAULT_ROLE);
        }
        dao.save(user);
    }

    @Transactional
    @Override
    public void update(User user) {
        dao.save(user);
    }

    @Transactional(readOnly = true)
    @Override
    public User read(Integer id) {
        return dao.findById(id).orElse(null);
    }

    @Transactional
    @Override
    public void delete(Integer id) {
        Optional<User> user = dao.findById(id);
        user.ifPresent(dao::delete);
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> listAll() {
        return dao.findAll();
    }

    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return dao.findByEmail(email).orElse(null);
    }

    @Transactional(readOnly = true)
    public User findByPhone(Phone phone) {
        return dao.findByPhone(phone).orElse(null);
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return dao.findByUsername(username).orElse(null);
    }

    @Transactional
    public void assignRole(User user, Role role) {
        user.setRole(role);

        dao.save(user);
    }

    @Transactional(readOnly = true)
    public boolean exists(String username) {
        return dao.findByUsername(username) != null;
    }

}
