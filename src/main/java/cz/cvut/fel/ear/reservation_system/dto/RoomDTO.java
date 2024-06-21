package cz.cvut.fel.ear.reservation_system.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomDTO {
    @JsonProperty("id")
    private Integer id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("capacity")
    private Integer capacity;

    @JsonProperty("hourlyRate")
    private Double hourlyRate;

    @JsonProperty("stornoFee")
    private Double stornoFee;
}
