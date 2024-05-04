package cz.cvut.fel.ear.reservation_system.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "users", schema = "public", catalog = "michajo6")
@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class)
@Getter
@Setter
public class User extends AbstractEntity {

    @OneToMany(mappedBy = "user")
    @JsonManagedReference(value="user-reservations")
    @JsonIgnore
    private Set<Reservation> reservations = new LinkedHashSet<>();

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "middle_name", length = 100)
    private String middleName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "email", nullable = false, length = 120)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Basic(optional = false)
    @Column(name = "password", nullable = false)
    private String password;

    @Basic(optional = false)
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Embedded
    private Phone phone;

    public void encodePassword(PasswordEncoder encoder) {
        this.password = encoder.encode(password);
    }

    public void erasePassword() {
        this.password = null;
    }

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }
}