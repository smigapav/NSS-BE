package cz.cvut.fel.ear.reservation_system.dao;

import cz.cvut.fel.ear.reservation_system.ReservationSystemApplication;
import cz.cvut.fel.ear.reservation_system.model.Role;
import cz.cvut.fel.ear.reservation_system.model.User;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(
        classes = ReservationSystemApplication.class,
        properties = "spring.config.name=application-test"
)@Transactional
@AutoConfigureTestEntityManager
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
class BaseDaoTest {


    @Autowired
    private TestEntityManager em;

    @Autowired
    private  UserDao sut;
    private User user;

    @BeforeEach
    void setUp() {
        user = em.find(User.class,44);
    }
    public User createTestUser(){
        User randomUser = new User();
        randomUser.setFirstName("test");
        randomUser.setLastName("test");
        randomUser.setEmail("test@test.cz");
        randomUser.setPassword("testtest");
        randomUser.setRole(Role.STANDARD_USER);
        randomUser.setUsername("testtest");
        randomUser.setPhone("testPhone");

        return randomUser;
    }
    @Test
    public void find() {
        User entity = sut.find(44);
        assertThat(user).isEqualTo(entity);
    }

    @Test
    public void findAll() {
        List<User> users = sut.findAll();
        assertFalse(users.isEmpty());
        assertThat(users).contains(user);


    }

    @Test
    public void persist() {

        User testUser = createTestUser();
        em.persist(testUser);
        sut.persist(testUser);

        List<User> found = sut.findAll();

        assertThat(found).contains(testUser);

    }


    @Test
    public void update() {
        User user = sut.find(44);

        user.setFirstName("Lilly");
        sut.update(user);

        User updatedUser = em.find(User.class, user.getId());

        assertThat(updatedUser.getFirstName()).isEqualTo("Lilly");
         }

    @Test
    public void remove() {
        User testUser = createTestUser();
        em.persist(testUser);
        sut.persist(testUser);

        List<User> found = sut.findAll();

        assertThat(found).contains(testUser);
        int id = testUser.getId();

        sut.remove(testUser);
        em.remove(testUser);

        User notFound = sut.find(id);
        assertNull(notFound);
    }

    @Test
    public void exists() {
        assertNotNull(user);
    }

}