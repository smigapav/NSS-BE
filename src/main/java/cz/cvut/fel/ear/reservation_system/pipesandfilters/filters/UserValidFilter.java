package cz.cvut.fel.ear.reservation_system.pipesandfilters.filters;

import cz.cvut.fel.ear.reservation_system.dto.UserDTO;
import cz.cvut.fel.ear.reservation_system.exception.UserNotFoundException;
import cz.cvut.fel.ear.reservation_system.exception.ValidationException;
import cz.cvut.fel.ear.reservation_system.pipesandfilters.Filter;
import cz.cvut.fel.ear.reservation_system.service.UserService;
import org.springframework.http.HttpStatus;

public class UserValidFilter implements Filter<UserDTO> {
    private final UserService userService;
    private final String username;

    public UserValidFilter(UserService userService, String username) {
        this.userService = userService;
        this.username = username;
    }

    /**
     * Executes the filter operation.
     * This method validates the user data.
     * If the username or email is null or empty, a ValidationException is thrown.
     * If the user does not exist, a UserNotFoundException is thrown.
     *
     * @param input the user data to validate
     * @return the validated user data
     * @throws ValidationException if the username or email is null or empty
     * @throws UserNotFoundException if the user does not exist
     */
    @Override
    public UserDTO execute(UserDTO input) {
        if (input.getUsername() == null || input.getUsername().isEmpty()) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Username cannot be null or empty.");
        }
        if (input.getEmail() == null || input.getEmail().isEmpty()) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "Email cannot be null or empty.");
        }
        if (userService.findByUsername(username) == null) {
            throw new UserNotFoundException(HttpStatus.NOT_FOUND, "User not found.");
        }
        return input;
    }
}