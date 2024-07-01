package cz.cvut.fel.ear.reservation_system.mapping;

import cz.cvut.fel.ear.reservation_system.dto.ReservationDTO;
import cz.cvut.fel.ear.reservation_system.model.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ReservationMapper {
    ReservationMapper INSTANCE = Mappers.getMapper(ReservationMapper.class);

    Reservation dtoToReservation(ReservationDTO reservationDTO);

    ReservationDTO reservationToDto(Reservation reservation);
}