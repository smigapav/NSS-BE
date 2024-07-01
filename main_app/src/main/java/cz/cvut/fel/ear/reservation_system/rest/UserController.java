package cz.cvut.fel.ear.reservation_system.rest;

import cz.cvut.fel.ear.reservation_system.dto.UserDTO;
import cz.cvut.fel.ear.reservation_system.mapping.UserMapper;
import cz.cvut.fel.ear.reservation_system.model.User;
import cz.cvut.fel.ear.reservation_system.rest.util.RestUtils;
import cz.cvut.fel.ear.reservation_system.security.model.UserDetails;
import cz.cvut.fel.ear.reservation_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rest/users")
@RequiredArgsConstructor
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    /**
     * Registers a new user.
     *
     * @param user User data
     */
    @PreAuthorize("(!#user.isAdmin() && anonymous) || hasAuthority('ADMIN')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> register(@RequestBody User user) {
        userService.create(user);
        LOG.info("User {} successfully registered.", user);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/current");
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @GetMapping(value = "current", produces = MediaType.APPLICATION_JSON_VALUE)
    public User getCurrent(Authentication auth) {
        assert auth.getPrincipal() instanceof UserDetails;
        LOG.info("User {} requested current user.", ((UserDetails) auth.getPrincipal()).getUser());
        return ((UserDetails) auth.getPrincipal()).getUser();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping(value = "edit/{username}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> editUser(@PathVariable String username, @RequestBody UserDTO updatedUserDTO) {
        try {
            User user = userService.editUserIfPossible(updatedUserDTO, username);
            LOG.info("User {} successfully updated by admin.", user);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (UsernameNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping(value = "all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserDTO> listAllUsers() {
        List<User> users = userService.listAll();
        LOG.info("Admin requested list of all users.");
        return users.stream()
                .map(UserMapper.INSTANCE::userToDto)
                .collect(Collectors.toList());
    }

    @PutMapping(value = "editSelf", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> editSelf(Authentication authentication, @RequestBody UserDTO updatedUserDTO) {
        try {
            User user = userService.editUserIfPossible(updatedUserDTO, authentication.getName());
            LOG.info("User {} successfully updated by admin.", user);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (UsernameNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(value = "delete", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteUser(@RequestBody UserDTO updatedUserDTO) {
        try {
            User user = userService.read(updatedUserDTO.getId());
            userService.delete(updatedUserDTO.getId());
            LOG.info("User {} successfully deleted by admin.", user);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (UsernameNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

