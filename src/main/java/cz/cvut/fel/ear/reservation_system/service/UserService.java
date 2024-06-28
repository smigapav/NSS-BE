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

/**
 * Service class for managing users.
 * This class provides CRUD operations for users and methods to find users by email, phone, and username.
 */
@Service
@RequiredArgsConstructor
public class UserService implements CRUDOperations<User> {

    /**
     * DAO for accessing user data.
     */
    private final UserDao dao;

    /**
     * Password encoder for encoding user passwords.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Creates a new user.
     *
     * @param user the user to create
     * @throws NullPointerException if the provided user is null
     */
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

    /**
     * Updates an existing user.
     *
     * @param user the user to update
     */
    @Transactional
    @Override
    public void update(User user) {
        dao.save(user);
    }

    /**
     * Reads a user by its ID.
     *
     * @param id the ID of the user to read
     * @return the user with the provided ID, or null if no such user exists
     */
    @Transactional(readOnly = true)
    @Override
    public User read(Integer id) {
        return dao.findById(id).orElse(null);
    }

    /**
     * Deletes a user by its ID.
     *
     * @param id the ID of the user to delete
     */
    @Transactional
    @Override
    public void delete(Integer id) {
        Optional<User> user = dao.findById(id);
        user.ifPresent(dao::delete);
    }

    /**
     * Lists all users.
     *
     * @return a list of all users
     */
    @Transactional(readOnly = true)
    @Override
    public List<User> listAll() {
        return dao.findAll();
    }

    /**
     * Finds a user by its email.
     *
     * @param email the email of the user to find
     * @return the user with the provided email, or null if no such user exists
     */
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return dao.findByEmail(email).orElse(null);
    }

    /**
     * Finds a user by its phone.
     *
     * @param phone the phone of the user to find
     * @return the user with the provided phone, or null if no such user exists
     */
    @Transactional(readOnly = true)
    public User findByPhone(Phone phone) {
        return dao.findByPhone(phone).orElse(null);
    }

    /**
     * Finds a user by its username.
     *
     * @param username the username of the user to find
     * @return the user with the provided username, or null if no such user exists
     */
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return dao.findByUsername(username).orElse(null);
    }

    /**
     * Assigns a role to a user.
     *
     * @param user the user to assign the role to
     * @param role the role to assign
     */
    @Transactional
    public void assignRole(User user, Role role) {
        user.setRole(role);
        dao.save(user);
    }

    /**
     * Checks if a user exists by its username.
     *
     * @param username the username of the user to check
     * @return true if the user exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean exists(String username) {
        return dao.findByUsername(username).isPresent();
    }

    /**
     * Edits a user if possible.
     *
     * @param updatedUserDTO the updated user data
     * @param username       the username of the user to edit
     * @return the edited user, or null if the user could not be edited
     */
    @Transactional
    public User editUserIfPossible(UserDTO updatedUserDTO, String username) {

        Pipeline<UserDTO> pipeline = new Pipeline<>();
        pipeline.addFilter(new GenericLoggingFilter<>("editing user if possible", UserService.class.getName(), "editUserIfPossible"));
        pipeline.addFilter(new UserValidFilter(this, username));
        pipeline.addFilter(new UserTransformationFilter(this, username, passwordEncoder));

        UserDTO processedUserDTO = pipeline.execute(updatedUserDTO);

        User existingUser = UserMapper.INSTANCE.dtoToUser(processedUserDTO);

        update(existingUser);

        return existingUser;
    }
}
