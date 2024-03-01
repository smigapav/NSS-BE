package cz.cvut.fel.ear.reservation_system.dao;

import cz.cvut.fel.ear.reservation_system.model.Room;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;

@Repository
public class RoomDao extends BaseDao<Room> {

    public RoomDao() { super(Room.class); }

    public Room findByName(String name) {
        try {
            return em.createNamedQuery("Room.findByName", Room.class)
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}