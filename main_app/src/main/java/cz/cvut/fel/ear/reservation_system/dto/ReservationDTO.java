package cz.cvut.fel.ear.reservation_system.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.cvut.fel.ear.reservation_system.model.ReservationStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReservationDTO {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("user")
    private UserDTO user;

    @JsonProperty("room")
    private RoomDTO room;

    @JsonProperty("dateFrom")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dateFrom;

    @JsonProperty("dateTo")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dateTo;

    @JsonProperty("createdAt")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAt;

    @JsonProperty("status")
    private ReservationStatus status;

    public ReservationDTO() {
    }
}
