package cz.cvut.fel.ear.reservation_system.dao;

import cz.cvut.fel.ear.reservation_system.model.Reservation;
import cz.cvut.fel.ear.reservation_system.model.ReservationStatus;
import cz.cvut.fel.ear.reservation_system.model.Room;
import cz.cvut.fel.ear.reservation_system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationDao extends JpaRepository<Reservation, Integer> {
    List<Reservation> findByUser(User user);

    List<Reservation> findByStatus(ReservationStatus status);

    List<Reservation> findByRoom(Room room);

    @Query("SELECT r FROM Reservation r WHERE r.room = :room AND r.status IN (:statuses)")
    List<Reservation> findByRoomAndActive(@Param("room") Room room,
                                          @Param("statuses") List<ReservationStatus> statuses);
}
