package org.codevoke.probnb.service;

import lombok.RequiredArgsConstructor;
import org.codevoke.probnb.dto.RoomDTO;
import org.codevoke.probnb.exceptions.RoomException;
import org.codevoke.probnb.model.Room;
import org.codevoke.probnb.model.User;
import org.codevoke.probnb.repository.RoomRepository;
import org.codevoke.probnb.utils.CopyUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final UserService userService;
    private final RoomRepository roomRepository;

    public RoomDTO findRoomById(Long id, Long ignored) {
        return convertToDTO(findRoomEntityById(id));
    }

    protected Room findRoomEntityById(Long id) {
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
}
