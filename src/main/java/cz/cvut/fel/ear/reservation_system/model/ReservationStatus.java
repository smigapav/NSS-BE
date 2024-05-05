package cz.cvut.fel.ear.reservation_system.model;

public enum ReservationStatus {
    NOT_PAID("NOT_PAID"), CANCELLED("CANCELLED"), PAID("PAID"), STORNO_REQUEST("STORNO_REQUEST"),

    COMPLETED("COMPLETED");

    private final String status;

    ReservationStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return status;
    }
}
