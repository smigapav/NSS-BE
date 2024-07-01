package cz.cvut.fel.ear.reservation_system.util;

import cz.cvut.fel.ear.reservation_system.model.ReservationStatus;
import cz.cvut.fel.ear.reservation_system.model.Role;

public final class Constants {

    /**
     * Default user role.
     */
    public static final Role DEFAULT_ROLE = Role.STANDARD_USER;
    public static final ReservationStatus DEFAULT_STATUS = ReservationStatus.NOT_PAID;

    /**
     * Username login form parameter.
     */
    public static final String USERNAME_PARAM = "username";

    private Constants() {
        throw new AssertionError();
    }
}
