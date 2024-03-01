package cz.cvut.fel.ear.reservation_system.dao;

import cz.cvut.fel.ear.reservation_system.model.Equipment;
import org.springframework.stereotype.Repository;

@Repository
public class EquipmentDao extends BaseDao<Equipment> {

    public EquipmentDao() { super(Equipment.class); }
}
