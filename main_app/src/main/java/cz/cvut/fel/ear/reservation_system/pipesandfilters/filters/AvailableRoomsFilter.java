package cz.cvut.fel.ear.reservation_system.pipesandfilters.filters;

import cz.cvut.fel.ear.reservation_system.dao.RoomDao;
import cz.cvut.fel.ear.reservation_system.model.Room;
import cz.cvut.fel.ear.reservation_system.pipesandfilters.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class AvailableRoomsFilter implements Filter<List<Room>> {

    private final RoomDao roomDao;
    private final RoomAvailabilityFilter roomAvailabilityFilter;

    @Autowired
    public AvailableRoomsFilter(RoomDao roomDao, RoomAvailabilityFilter roomAvailabilityFilter) {
        this.roomDao = roomDao;
        this.roomAvailabilityFilter = roomAvailabilityFilter;
    }

    public void setDates(LocalDateTime from, LocalDateTime to) {
        roomAvailabilityFilter.setFrom(from);
        roomAvailabilityFilter.setTo(to);
    }

    @Override
    public List<Room> execute(List<Room> rooms) {
        return rooms.stream()
                .map(roomAvailabilityFilter::execute)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}