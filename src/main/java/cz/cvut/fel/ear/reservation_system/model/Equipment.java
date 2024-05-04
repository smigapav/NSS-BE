package cz.cvut.fel.ear.reservation_system.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "equipment", schema = "public")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Getter
@Setter
public class Equipment extends AbstractEntity {
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @ManyToMany(mappedBy = "equipment")
    @JsonBackReference(value="room-equipment")
    private Set<Room> rooms;
}