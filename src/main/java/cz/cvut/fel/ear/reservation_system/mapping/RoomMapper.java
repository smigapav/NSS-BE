package cz.cvut.fel.ear.reservation_system.mapping;

import cz.cvut.fel.ear.reservation_system.dto.RoomDTO;
import cz.cvut.fel.ear.reservation_system.model.Room;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RoomMapper {
    RoomMapper INSTANCE = Mappers.getMapper(RoomMapper.class);

    Room dtoToRoom(RoomDTO roomDTO);
    RoomDTO roomToDto(Room room);
}
