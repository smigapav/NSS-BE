package cz.cvut.fel.ear.reservation_system.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "room", schema = "public")
@NamedQueries({
        @NamedQuery(name = "Room.findByName", query = "SELECT r FROM Room r WHERE r.name = :name")
})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
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
    @JoinTable(name = "room_equipment",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "equipment_id"))
    private Set<Equipment> equipment;

    @OneToMany(mappedBy = "room")
    @JsonManagedReference(value="room-reservations")
    @JsonIgnore
    private Set<Reservation> reservations = new LinkedHashSet<>();

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(Double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public Double getStornoFee() {
        return stornoFee;
    }

    public void setStornoFee(Double stornoFee) {
        this.stornoFee = stornoFee;
    }

    public Set<Equipment> getEquipment() {
        return equipment;
    }

    public void setEquipment(Set<Equipment> equipment) {
        this.equipment = equipment;
    }

    public Set<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(Set<Reservation> reservations) {
        this.reservations = reservations;
    }

}