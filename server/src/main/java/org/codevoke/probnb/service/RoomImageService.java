package org.codevoke.probnb.service;

import lombok.RequiredArgsConstructor;
import org.codevoke.probnb.dto.RoomImageDTO;
import org.codevoke.probnb.exceptions.RoomImageException;
import org.codevoke.probnb.model.Image;
import org.codevoke.probnb.model.Room;
import org.codevoke.probnb.model.RoomImage;
import org.codevoke.probnb.repository.RoomImageRepository;
import org.codevoke.probnb.utils.CopyUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomImageService {
    private final RoomImageRepository roomImageRepository;
    private final RoomService roomService;
    private final ImageService imageService;

    public RoomImageDTO findRoomImageById(Long id) {
        return convertToDTO(findRoomImageEntityById(id));
    }

    public List<RoomImageDTO> findRoomImagesByRoomId(Long roomId) {
        return roomImageRepository.findAll().stream()
                .filter(roomImage -> roomImage.getRoom().getId().equals(roomId))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public RoomImage findRoomImageEntityById(Long id) {
        return roomImageRepository.findById(id)
                .orElseThrow(() -> RoomImageException.RoomImageNotFound(id));
    }

    public RoomImageDTO createRoomImage(RoomImageDTO roomImageDTO) {
        Room room = roomService.findRoomEntityById(roomImageDTO.getRoomId());
        Image image = imageService.getImageEntityById(roomImageDTO.getImageId());

        RoomImage roomImage = convertToEntity(roomImageDTO);
        roomImage.setRoom(room);
        roomImage.setImage(image);
        
        roomImageRepository.save(roomImage);
        return convertToDTO(roomImage);
    }

    public RoomImageDTO updateRoomImage(Long roomImageId, RoomImageDTO roomImageDTO) {
        RoomImage target = findRoomImageEntityById(roomImageId);
        CopyUtils.copyNonNullProperties(roomImageDTO, target);
        roomImageRepository.save(target);
        return convertToDTO(target);
    }

    public RoomImageDTO deleteRoomImage(Long roomImageId) {
        RoomImage target = findRoomImageEntityById(roomImageId);
        roomImageRepository.delete(target);
        return convertToDTO(target);
    }

    public RoomImage convertToEntity(RoomImageDTO roomImageDTO) {
        RoomImage roomImage = new RoomImage();
        BeanUtils.copyProperties(roomImageDTO, roomImage);
        return roomImage;
    }

    public RoomImageDTO convertToDTO(RoomImage roomImage) {
        RoomImageDTO roomImageDTO = new RoomImageDTO();
        BeanUtils.copyProperties(roomImage, roomImageDTO);
        
        // Устанавливаем ID комнаты и изображения
        if (roomImage.getRoom() != null) {
            roomImageDTO.setRoomId(roomImage.getRoom().getId());
        }
        if (roomImage.getImage() != null) {
            roomImageDTO.setImageId(roomImage.getImage().getId());
        }
        
        return roomImageDTO;
    }
} 