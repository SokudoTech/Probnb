package org.codevoke.probnb.repository;

import org.codevoke.probnb.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByRoomTypeContainingIgnoreCase(String roomType);
    List<Room> findByRoomsCount(Integer roomsCount);
    List<Room> findByHostId(Long hostId);
    List<Room> findByLocationContainingIgnoreCase(String location);
    List<Room> findByRoomTypeContainingIgnoreCaseAndRoomsCount(String roomType, Integer roomsCount);
    List<Room> findByRoomTypeContainingIgnoreCaseAndLocationContainingIgnoreCase(String roomType, String location);
    List<Room> findByRoomsCountAndLocationContainingIgnoreCase(Integer roomsCount, String location);
    List<Room> findByRoomTypeContainingIgnoreCaseAndRoomsCountAndLocationContainingIgnoreCase(String roomType, Integer roomsCount, String location);
}
