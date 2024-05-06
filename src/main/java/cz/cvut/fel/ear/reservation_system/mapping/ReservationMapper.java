package cz.cvut.fel.ear.reservation_system.mapping;

import cz.cvut.fel.ear.reservation_system.dto.ReservationDTO;
import cz.cvut.fel.ear.reservation_system.model.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ReservationMapper {
    ReservationMapper INSTANCE = Mappers.getMapper(ReservationMapper.class);

    @Mapping(source = "user", target = "user")
    @Mapping(source = "room", target = "room")
    @Mapping(source = "dateFrom", target = "dateFrom")
    @Mapping(source = "dateTo", target = "dateTo")
    @Mapping(source = "status", target = "status")
    Reservation dtoToReservation(ReservationDTO reservationDTO);

    @Mapping(source = "user", target = "user")
    @Mapping(source = "room", target = "room")
    @Mapping(source = "dateFrom", target = "dateFrom")
    @Mapping(source = "dateTo", target = "dateTo")
    @Mapping(source = "status", target = "status")
    ReservationDTO reservationToDto(Reservation reservation);
}