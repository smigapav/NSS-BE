package cz.cvut.fel.ear.reservation_system.service;

import cz.cvut.fel.ear.reservation_system.environment.Generator;
import cz.cvut.fel.ear.reservation_system.model.Role;
import cz.cvut.fel.ear.reservation_system.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@AutoConfigureTestEntityManager
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private final User user = Generator.generateUser();
    @Autowired
    private TestEntityManager em;
    @Autowired
    private UserService sut;

    @BeforeEach
    public void setUp() {
        user.setRole(Role.STANDARD_USER);
        em.persist(user);
    }

    @Test
    public void persistSetsRoleToStandardUser() {
        sut.create(user);

        User newUser = em.find(User.class, user.getId());

        assertEquals(Role.STANDARD_USER, newUser.getRole());
    }

    @Test
    public void findByEmailReturnsCorrectly() {
        User newUser = sut.findByEmail(user.getEmail());

        assertEquals(user.getId(), newUser.getId());
    }

    @Test
    public void findByPhoneReturnsCorrectly() {
        User newUser = sut.findByPhone(user.getPhone());

        assertEquals(user.getId(), newUser.getId());
    }

    @Test
    public void assignRoleAssignsCorrectly() {
        sut.assignRole(user, Role.STANDARD_USER);

        User newUser = em.find(User.class, user.getId());

        assertEquals(Role.STANDARD_USER, newUser.getRole());
    }

}
