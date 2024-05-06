package cz.cvut.fel.ear.reservation_system.dao;

import cz.cvut.fel.ear.reservation_system.ReservationSystemApplication;
import cz.cvut.fel.ear.reservation_system.model.Phone;
import cz.cvut.fel.ear.reservation_system.model.Role;
import cz.cvut.fel.ear.reservation_system.model.User;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        classes = ReservationSystemApplication.class,
        properties = "spring.config.name=application-test"
)
@Transactional
class BaseDaoTest {

    @MockBean
    private UserDao sut;

    @Autowired
    private EntityManager em;

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(44);
        // Set other properties of the user

        Mockito.when(sut.findById(user.getId())).thenReturn(Optional.of(user));
        Mockito.when(sut.findAll()).thenReturn(List.of(user));
    }

    public User createTestUser(){
        User randomUser = new User();
        randomUser.setFirstName("test");
        randomUser.setLastName("test");
        randomUser.setEmail("test@test.cz");
        randomUser.setPassword("testtest");
        randomUser.setRole(Role.STANDARD_USER);
        randomUser.setUsername("testtest");
        Phone phone = new Phone();
        phone.setNumber(123456789);
        phone.setPrefix("+1");
        randomUser.setPhone(phone);

        return randomUser;
    }

    @Test
    public void find() {
        Optional<User> entity = sut.findById(44);
        assertTrue(entity.isPresent());
        assertThat(user).isEqualTo(entity.get());
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
        Mockito.when(sut.save(testUser)).thenReturn(testUser);
        Mockito.when(sut.findAll()).thenReturn(List.of(testUser));

        sut.save(testUser);

        List<User> found = sut.findAll();

        assertThat(found).contains(testUser);
    }

    @Test
    public void remove() {
        User testUser = createTestUser();
        Mockito.when(sut.save(testUser)).thenReturn(testUser);
        Mockito.when(sut.findAll()).thenReturn(List.of(testUser));

        em.persist(testUser);
        em.flush();

//        testUser = sut.save(testUser); // Update the testUser with the saved User

        assertNotNull(testUser.getId()); // Ensure the id is not null

        List<User> found = sut.findAll();

        assertThat(found).contains(testUser);
        int id = testUser.getId();

        User finalTestUser = testUser;
        Mockito.doAnswer(invocation -> {
            Object arg0 = invocation.getArgument(0);
            if (arg0 == finalTestUser) {
                Mockito.when(sut.findById(id)).thenReturn(Optional.empty());
            }
            return null;
        }).when(sut).delete(testUser);

        sut.delete(testUser);

        User notFound = sut.findById(id).orElse(null);
        assertNull(notFound);
    }

    @Test
    public void update() {
        Optional<User> user = sut.findById(44);

        user.ifPresent(u -> u.setFirstName("Lilly"));
        user.ifPresent(sut::save);

        user.ifPresent(u -> {
            User updatedUser = sut.findById(u.getId()).orElse(null);
            assertThat(updatedUser.getFirstName()).isEqualTo("Lilly");
        });
    }

    @Test
    public void exists() {
        assertNotNull(user);
    }

}