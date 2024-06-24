package cz.cvut.fel.ear.reservation_system.service;

import cz.cvut.fel.ear.reservation_system.dao.UserDao;
import cz.cvut.fel.ear.reservation_system.dto.UserDTO;
import cz.cvut.fel.ear.reservation_system.mapping.UserMapper;
import cz.cvut.fel.ear.reservation_system.model.Phone;
import cz.cvut.fel.ear.reservation_system.model.Role;
import cz.cvut.fel.ear.reservation_system.model.User;
import cz.cvut.fel.ear.reservation_system.pipesandfilters.Pipeline;
import cz.cvut.fel.ear.reservation_system.pipesandfilters.filters.GenericLoggingFilter;
import cz.cvut.fel.ear.reservation_system.pipesandfilters.filters.UserTransformationFilter;
import cz.cvut.fel.ear.reservation_system.pipesandfilters.filters.UserValidFilter;
import cz.cvut.fel.ear.reservation_system.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements CRUDOperations<User> {

    private final UserDao dao;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public void create(User user) {
        Objects.requireNonNull(user);
        user.setRole(Role.STANDARD_USER);
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
        return dao.findByUsername(username).isPresent();
    }

    @Transactional
    public User editUserIfPossible(UserDTO updatedUserDTO, String username) {

    Pipeline<UserDTO> pipeline = new Pipeline<>();
    pipeline.addFilter(new GenericLoggingFilter<>());
    pipeline.addFilter(new UserValidFilter(this, username));
    pipeline.addFilter(new UserTransformationFilter(this, username, passwordEncoder));

    UserDTO processedUserDTO = pipeline.execute(updatedUserDTO);

    User existingUser = UserMapper.INSTANCE.dtoToUser(processedUserDTO);

    update(existingUser);

    return existingUser;
}
}
