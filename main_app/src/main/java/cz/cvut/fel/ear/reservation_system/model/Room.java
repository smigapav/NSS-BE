package cz.cvut.fel.ear.reservation_system.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "room", schema = "public")
@NamedQueries({@NamedQuery(name = "Room.findByName", query = "SELECT r FROM Room r WHERE r.name = :name")})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Getter
@Setter
public class Room extends AbstractEntity {

    @Column(name = "capacity", nullable = false)
    @OrderBy
    private Integer capacity;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "hourly_rate", nullable = false)
    private Double hourlyRate;

    @Column(name = "storno_fee", nullable = false)
    private Double stornoFee;

    @ManyToMany
    @JoinTable(name = "room_equipment", joinColumns = @JoinColumn(name = "room_id"), inverseJoinColumns = @JoinColumn(name = "equipment_id"))
    private Set<Equipment> equipment;

    @OneToMany(mappedBy = "room")
    @JsonManagedReference(value = "room-reservations")
    @JsonIgnore
    private Set<Reservation> reservations = new LinkedHashSet<>();
}
