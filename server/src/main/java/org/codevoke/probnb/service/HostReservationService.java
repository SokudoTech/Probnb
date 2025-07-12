package org.codevoke.probnb.service;

import lombok.RequiredArgsConstructor;
import org.codevoke.probnb.dto.HostReservationDTO;
import org.codevoke.probnb.exceptions.HostReservationException;
import org.codevoke.probnb.model.HostReservation;
import org.codevoke.probnb.model.Room;
import org.codevoke.probnb.model.User;
import org.codevoke.probnb.repository.HostReservationRepository;
import org.codevoke.probnb.utils.CopyUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HostReservationService {
    private final HostReservationRepository hostReservationRepository;
    private final UserService userService;
    private final RoomService roomService;

    public HostReservationDTO findHostReservationById(Long id) {
        return convertToDTO(findHostReservationEntityById(id));
    }

    public List<HostReservationDTO> findHostReservationsByHostId(Long hostId) {
        return hostReservationRepository.findAll().stream()
                .filter(reservation -> reservation.getHost().getId().equals(hostId))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<HostReservationDTO> findHostReservationsByRoomId(Long roomId) {
        return hostReservationRepository.findAll().stream()
                .filter(reservation -> reservation.getRoom().getId().equals(roomId))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    protected HostReservation findHostReservationEntityById(Long id) {
        return hostReservationRepository.findById(id)
                .orElseThrow(() -> HostReservationException.HostReservationNotFound(id));
    }

    public HostReservationDTO createHostReservation(HostReservationDTO hostReservationDTO, Long hostId) {
        User host = userService.getUserEntityById(hostId);
        Room room = roomService.findRoomEntityById(hostReservationDTO.getRoomId());
        
        // Проверяем, что пользователь является хостом этой комнаты
        if (room.getHost() == null || !room.getHost().getId().equals(hostId)) {
            throw HostReservationException.HostReservationNotFound(hostId);
        }

        HostReservation hostReservation = convertToEntity(hostReservationDTO);
        hostReservation.setHost(host);
        hostReservation.setRoom(room);
        
        hostReservationRepository.save(hostReservation);
        return convertToDTO(hostReservation);
    }

    public HostReservationDTO updateHostReservation(Long reservationId, HostReservationDTO hostReservationDTO) {
        HostReservation target = findHostReservationEntityById(reservationId);
        CopyUtils.copyNonNullProperties(hostReservationDTO, target);
        hostReservationRepository.save(target);
        return convertToDTO(target);
    }

    public HostReservationDTO deleteHostReservation(Long reservationId) {
        HostReservation target = findHostReservationEntityById(reservationId);
        hostReservationRepository.delete(target);
        return convertToDTO(target);
    }

    public HostReservation convertToEntity(HostReservationDTO hostReservationDTO) {
        HostReservation hostReservation = new HostReservation();
        BeanUtils.copyProperties(hostReservationDTO, hostReservation);
        
        // Конвертируем ZonedDateTime в Instant
        if (hostReservationDTO.getDateStart() != null) {
            hostReservation.setStartDate(hostReservationDTO.getDateStart().toInstant());
        }
        if (hostReservationDTO.getDateEnd() != null) {
            hostReservation.setEndDate(hostReservationDTO.getDateEnd().toInstant());
        }
        
        return hostReservation;
    }

    public HostReservationDTO convertToDTO(HostReservation hostReservation) {
        HostReservationDTO hostReservationDTO = new HostReservationDTO();
        BeanUtils.copyProperties(hostReservation, hostReservationDTO);
        
        // Устанавливаем ID комнаты и хоста
        if (hostReservation.getRoom() != null) {
            hostReservationDTO.setRoomId(hostReservation.getRoom().getId());
        }
        if (hostReservation.getHost() != null) {
            hostReservationDTO.setHostId(hostReservation.getHost().getId());
        }
        
        // Конвертируем Instant в ZonedDateTime
        if (hostReservation.getStartDate() != null) {
            hostReservationDTO.setDateStart(hostReservation.getStartDate().atZone(ZoneId.systemDefault()));
        }
        if (hostReservation.getEndDate() != null) {
            hostReservationDTO.setDateEnd(hostReservation.getEndDate().atZone(ZoneId.systemDefault()));
        }
        
        return hostReservationDTO;
    }
} 