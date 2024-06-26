package cz.cvut.fel.ear.reservation_system.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "order", schema = "public")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Getter
@Setter
public class Order extends AbstractEntity {

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "total_price")
    private Double totalPrice;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reservation", nullable = false)
    private Reservation reservation;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public static class Builder {
        private LocalDateTime createdAt;
        private LocalDateTime confirmedAt;
        private Double totalPrice;
        private Reservation reservation;
        private User user;

        public Builder withCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder withConfirmedAt(LocalDateTime confirmedAt) {
            this.confirmedAt = confirmedAt;
            return this;
        }

        public Builder withTotalPrice(Double totalPrice) {
            this.totalPrice = totalPrice;
            return this;
        }

        public Builder withReservation(Reservation reservation) {
            this.reservation = reservation;
            return this;
        }

        public Builder withUser(User user) {
            this.user = user;
            return this;
        }

        public Order build() {
            Order order = new Order();
            order.createdAt = this.createdAt;
            order.confirmedAt = this.confirmedAt;
            order.totalPrice = this.totalPrice;
            order.reservation = this.reservation;
            order.user = this.user;
            return order;
        }
    }
}
