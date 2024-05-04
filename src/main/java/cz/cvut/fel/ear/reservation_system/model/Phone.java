package cz.cvut.fel.ear.reservation_system.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class Phone {

    @Column(name = "phone_number", nullable = false)
    private int number;

    @Column(name = "phone_prefix", nullable = false, length = 10)
    private String prefix;
}