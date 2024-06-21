package cz.cvut.fel.ear.reservation_system.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import cz.cvut.fel.ear.reservation_system.model.ReservationStatus;
import cz.cvut.fel.ear.reservation_system.model.Room;
import cz.cvut.fel.ear.reservation_system.model.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReservationDTO {
    @JsonProperty("id")
    private Integer id;

    @JsonIgnore
    private User user;

    @JsonIgnore
    private Room room;

    @JsonProperty("dateFrom")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dateFrom;

    @JsonProperty("dateTo")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dateTo;

    @JsonProperty("status")
    private ReservationStatus status;

    public ReservationDTO() {
    }
}
