package cz.cvut.fel.ear.reservation_system.service.security;

import cz.cvut.fel.ear.reservation_system.dao.UserDao;
import cz.cvut.fel.ear.reservation_system.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserDao userDao;

    public UserDetailsService(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final User user = userDao.findByUsername(username).orElse(null);
        if (user == null) {
            throw new UsernameNotFoundException("User with username " + username + " not found.");
        }
        return new cz.cvut.fel.ear.reservation_system.security.model.UserDetails(user);
    }
}
