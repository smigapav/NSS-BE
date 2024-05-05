package cz.cvut.fel.ear.reservation_system.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.cvut.fel.ear.reservation_system.model.Reservation;

import java.time.LocalDateTime;

public class ReservationDTO {
    @JsonProperty("id")
    private int id;
    @JsonProperty("dateFrom")
    private LocalDateTime dateFrom;
    @JsonProperty("dateTo")
    private LocalDateTime dateTo;
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;
    @JsonProperty("status")
    private String status;

    @JsonProperty("userId")
    private int userId;

    @JsonProperty("userName")
    private String userName;

    @JsonProperty("roomId")
    private int roomId;

    @JsonProperty("roomName")
    private String roomName;

    @JsonProperty("confirmedAt")
    private LocalDateTime confirmedAt;

    @JsonProperty("totalPrice")
    private Double totalPrice;

    public ReservationDTO() {
    }
}
