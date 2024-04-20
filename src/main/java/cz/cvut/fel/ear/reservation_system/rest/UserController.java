package cz.cvut.fel.ear.reservation_system.rest;

import cz.cvut.fel.ear.reservation_system.model.Role;
import cz.cvut.fel.ear.reservation_system.model.User;
import cz.cvut.fel.ear.reservation_system.rest.util.RestUtils;
import cz.cvut.fel.ear.reservation_system.security.model.UserDetails;
import cz.cvut.fel.ear.reservation_system.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@RestController
@RequestMapping("/rest/users")
@CrossOrigin(origins="*")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Registers a new user.
     *
     * @param user User data
     */
    @PreAuthorize("(!#user.isAdmin() && anonymous) || hasAuthority('ADMIN')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> register(@RequestBody User user) {
        user.setRole(Role.STANDARD_USER);
        userService.persist(user);
        LOG.debug("User {} successfully registered.", user);
        final HttpHeaders headers = RestUtils.createLocationHeaderFromCurrentUri("/current");
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @GetMapping(value = "current", produces = MediaType.APPLICATION_JSON_VALUE)
    public User getCurrent(Authentication auth) {
        assert auth.getPrincipal() instanceof UserDetails;
        return ((UserDetails) auth.getPrincipal()).getUser();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping(value = "edit", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> editUser(@RequestBody User updatedUser) {

        User user = userService.findByUsername(updatedUser.getUsername());

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (updatedUser.getRole() != null) {
            user.setRole(updatedUser.getRole());
        }
        if (updatedUser.getEmail() != null) {
            user.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getPhone() != null) {
            user.setPhone(updatedUser.getPhone());
        }

        userService.persist(user);
        LOG.debug("User {} successfully updated by admin.", user);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}

