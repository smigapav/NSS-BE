package cz.cvut.fel.ear.reservation_system.model;

public enum Role {
    ADMIN("ADMIN"), STANDARD_USER("STANDARD_USER"), BANNED_USER("BANNED_USER");

    private final String name;

    Role(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
