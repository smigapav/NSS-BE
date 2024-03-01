package cz.cvut.fel.ear.reservation_system.environment;

import cz.cvut.fel.ear.reservation_system.model.Room;
import cz.cvut.fel.ear.reservation_system.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Random;

public class Generator {

    private static final Random RAND = new Random();

    public static int randomInt() {
        return RAND.nextInt();
    }

    public static int randomInt(int max) {
        return RAND.nextInt(max);
    }

    public static int randomInt(int min, int max) {
        assert min >= 0;
        assert min < max;

        int result;
        do {
            result = randomInt(max);
        } while (result < min);
        return result;
    }

    public static boolean randomBoolean() {
        return RAND.nextBoolean();
    }

    public static User generateUser() {
        final User user = new User();
        user.setFirstName("FirstName" + randomInt());
        user.setLastName("LastName" + randomInt());
        user.setEmail("user" + randomInt() + "@cvut.fel.cz");
        user.setPhone("+420 " + "777" + randomInt() + "654");
        user.setPassword("test"+ randomInt());
        user.setUsername("test" + randomInt());
        return user;
    }
}
