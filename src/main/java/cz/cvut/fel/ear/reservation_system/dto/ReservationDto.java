package cz.cvut.fel.ear.reservation_system.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.cvut.fel.ear.reservation_system.model.Reservation;

import java.time.LocalDateTime;

public class ReservationDto {
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

    public ReservationDto() {
    }

    public ReservationDto(Reservation reservation) {
        this.id = reservation.getId();
        this.dateFrom = reservation.getDateFrom();
        this.dateTo = reservation.getDateTo();
        this.createdAt = reservation.getCreatedAt();
        this.status = reservation.getStatus().toString();

        this.userId = reservation.getUser().getId();
        this.userName = reservation.getUser().getUsername();

        this.roomId = reservation.getRoom().getId();
        this.roomName = reservation.getRoom().getName();

        this.confirmedAt = reservation.getOrder().getConfirmedAt();
        this.totalPrice = reservation.getOrder().getTotalPrice();
    }

}
