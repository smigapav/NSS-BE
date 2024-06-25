package cz.cvut.fel.ear.reservation_system.pipesandfilters.filters;

import cz.cvut.fel.ear.reservation_system.dto.UserDTO;
import cz.cvut.fel.ear.reservation_system.mapping.UserMapper;
import cz.cvut.fel.ear.reservation_system.model.User;
import cz.cvut.fel.ear.reservation_system.pipesandfilters.Filter;
import cz.cvut.fel.ear.reservation_system.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

public class UserTransformationFilter implements Filter<UserDTO> {

    private final UserService userService;
    private final String username;
    private final PasswordEncoder passwordEncoder;

    public UserTransformationFilter(UserService userService, String username, PasswordEncoder encoder) {
        this.userService = userService;
        this.username = username;
        this.passwordEncoder = encoder;
    }

    @Override
    public UserDTO execute(UserDTO input) {
        User existingUser = userService.findByUsername(username);
        User updatedUser = UserMapper.INSTANCE.dtoToUser(input);

        Optional.ofNullable(updatedUser.getRole()).ifPresent(existingUser::setRole);
        Optional.ofNullable(updatedUser.getPassword()).ifPresent(password -> {
            existingUser.setPassword(password);
            existingUser.encodePassword(passwordEncoder);
        });
        Optional.ofNullable(updatedUser.getFirstName()).ifPresent(existingUser::setFirstName);
        Optional.ofNullable(updatedUser.getMiddleName()).ifPresent(existingUser::setMiddleName);
        Optional.ofNullable(updatedUser.getLastName()).ifPresent(existingUser::setLastName);
        Optional.ofNullable(updatedUser.getEmail()).ifPresent(existingUser::setEmail);
        Optional.ofNullable(updatedUser.getPhone()).ifPresent(existingUser::setPhone);

        return UserMapper.INSTANCE.userToDto(existingUser);
    }
}
