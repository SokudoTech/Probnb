package org.codevoke.probnb.service;

import lombok.RequiredArgsConstructor;
import org.codevoke.probnb.dto.RoomDTO;
import org.codevoke.probnb.dto.RoomFilterDTO;
import org.codevoke.probnb.dto.RoomSearchDTO;
import org.codevoke.probnb.exceptions.RoomException;
import org.codevoke.probnb.model.Room;
import org.codevoke.probnb.model.User;
import org.codevoke.probnb.repository.RoomRepository;
import org.codevoke.probnb.utils.CopyUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final UserService userService;
    private final RoomRepository roomRepository;

    public RoomDTO findRoomById(Long id, Long ignored) {
        return convertToDTO(findRoomEntityById(id));
    }

    public List<RoomDTO> findRoomsByHostId(Long hostId) {
        return roomRepository.findAll().stream()
                .filter(room -> room.getHost() != null && room.getHost().getId().equals(hostId))
                .map(RoomService::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<RoomDTO> filterRooms(RoomFilterDTO filter) {
        List<Room> rooms = getRoomsByBasicFilters(filter);
        
        // Если указаны даты, фильтруем по доступности
        if (filter.getCheckInDate() != null && filter.getCheckOutDate() != null) {
            rooms = filterRoomsByAvailability(
                rooms,
                filter.getCheckInDate(),
                filter.getCheckOutDate()
            );
        }

        return rooms.stream()
                .map(RoomService::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<RoomSearchDTO> searchRooms(RoomFilterDTO filter) {
        List<Room> rooms = getRoomsByBasicFilters(filter);
        
        // Если указаны даты, фильтруем по доступности
        if (filter.getCheckInDate() != null && filter.getCheckOutDate() != null) {
            rooms = filterRoomsByAvailability(rooms, filter.getCheckInDate(), filter.getCheckOutDate());
        }
        
        return rooms.stream()
                .map(RoomService::convertToSearchDTO)
                .collect(Collectors.toList());
    }

    private List<Room> getRoomsByBasicFilters(RoomFilterDTO filter) {
        String roomType = filter.getRoomType();
        Integer roomsCount = filter.getRoomsCount();
        String location = filter.getLocation();

        if (roomType != null && roomsCount != null && location != null) {
            return roomRepository.findByRoomTypeContainingIgnoreCaseAndRoomsCountAndLocationContainingIgnoreCase(roomType, roomsCount, location);
        } else if (roomType != null && roomsCount != null) {
            return roomRepository.findByRoomTypeContainingIgnoreCaseAndRoomsCount(roomType, roomsCount);
        } else if (roomType != null && location != null) {
            return roomRepository.findByRoomTypeContainingIgnoreCaseAndLocationContainingIgnoreCase(roomType, location);
        } else if (roomsCount != null && location != null) {
            return roomRepository.findByRoomsCountAndLocationContainingIgnoreCase(roomsCount, location);
        } else if (roomType != null) {
            return roomRepository.findByRoomTypeContainingIgnoreCase(roomType);
        } else if (roomsCount != null) {
            return roomRepository.findByRoomsCount(roomsCount);
        } else if (location != null) {
            return roomRepository.findByLocationContainingIgnoreCase(location);
        } else {
            return roomRepository.findAll();
        }
    }

    private List<Room> filterRoomsByAvailability(List<Room> rooms, ZonedDateTime checkIn, ZonedDateTime checkOut) {
        return rooms.stream()
                .filter(room -> isRoomAvailableForDates(room, checkIn, checkOut))
                .collect(Collectors.toList());
    }

    private boolean isRoomAvailableForDates(Room room, ZonedDateTime checkIn, ZonedDateTime checkOut) {
        Instant checkInInstant = checkIn.toInstant();
        Instant checkOutInstant = checkOut.toInstant();

        // Если у комнаты нет host reservations, значит хост сдает её всегда
        if (room.getHostReservations() == null || room.getHostReservations().isEmpty()) {
            // Проверяем только конфликты с обычными бронированиями
            if (room.getReservations() == null || room.getReservations().isEmpty()) {
                return true; // Нет бронирований - комната доступна
            }
            return room.getReservations().stream()
                    .noneMatch(reservation -> 
                        (reservation.getStartDate().isBefore(checkOutInstant) && 
                         reservation.getEndDate().isAfter(checkInInstant)));
        }

        // Проверяем, что хост сдает комнату в эти даты
        boolean hostAvailable = room.getHostReservations().stream()
                .anyMatch(hostReservation -> 
                    hostReservation.getStartDate().isBefore(checkInInstant) && 
                    hostReservation.getEndDate().isAfter(checkOutInstant));

        if (!hostAvailable) {
            return false;
        }

        // Проверяем, что нет конфликтующих бронирований
        boolean noConflicts = true;
        if (room.getReservations() != null && !room.getReservations().isEmpty()) {
            noConflicts = room.getReservations().stream()
                    .noneMatch(reservation -> 
                        (reservation.getStartDate().isBefore(checkOutInstant) && 
                         reservation.getEndDate().isAfter(checkInInstant)));
        }

        return noConflicts;
    }

    public Room findRoomEntityById(Long id) {
        return roomRepository.findById(id).orElseThrow(() -> RoomException.RoomNotFound(id));
    }

    public RoomDTO createRoom(RoomDTO roomDTO, Long userId) {
        User user = userService.getUserEntityById(userId);
        Room room = convertToEntity(roomDTO);
        room.setHost(user);
        roomRepository.save(room);
        return convertToDTO(room);
    }

    public RoomDTO patchRoom(Long roomId, RoomDTO roomDTO) {
        Room target = findRoomEntityById(roomId);
        CopyUtils.copyNonNullProperties(roomDTO, target);
        roomRepository.save(target);
        return convertToDTO(target);
    }

    public RoomDTO deleteRoom(Long roomId) {
        Room target = findRoomEntityById(roomId);
        roomRepository.delete(target);
        return convertToDTO(target);
    }

    public Room convertToEntity(RoomDTO roomDTO) {
        Room room = new Room();
        BeanUtils.copyProperties(roomDTO, room);
        return room;
    }

    public static RoomDTO convertToDTO(Room room) {
        RoomDTO roomDTO = new RoomDTO();
        BeanUtils.copyProperties(room, roomDTO);
        return roomDTO;
    }

    public static RoomSearchDTO convertToSearchDTO(Room room) {
        RoomSearchDTO roomSearchDTO = new RoomSearchDTO();
        roomSearchDTO.setId(room.getId());
        roomSearchDTO.setTitle(room.getTitle());
        roomSearchDTO.setSubtitle(room.getSubtitle());
        
        // Устанавливаем URL первого изображения, если есть
        if (room.getImages() != null && !room.getImages().isEmpty()) {
            roomSearchDTO.setImageUrl("/images/" + room.getImages().get(0).getImage().getId());
        }
        
        return roomSearchDTO;
    }
}
