package org.codevoke.probnb.service;

import lombok.RequiredArgsConstructor;
import org.codevoke.probnb.dto.ReservationDTO;
import org.codevoke.probnb.exceptions.ReservationException;
import org.codevoke.probnb.model.Reservation;
import org.codevoke.probnb.model.Room;
import org.codevoke.probnb.model.User;
import org.codevoke.probnb.repository.ReservationRepository;
import org.codevoke.probnb.utils.CopyUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final UserService userService;
    private final RoomService roomService;

    public ReservationDTO findReservationById(Long id) {
        return convertToDTO(findReservationEntityById(id));
    }

    public List<ReservationDTO> findReservationsByUserId(Long userId) {
        return reservationRepository.findAll().stream()
                .filter(reservation -> reservation.getGuest().getId().equals(userId))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ReservationDTO> findReservationsByHostId(Long hostId) {
        return reservationRepository.findAll().stream()
                .filter(reservation -> reservation.getHost().getId().equals(hostId))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ReservationDTO> findReservationsByRoomId(Long roomId) {
        return reservationRepository.findAll().stream()
                .filter(reservation -> reservation.getRoom().getId().equals(roomId))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    protected Reservation findReservationEntityById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> ReservationException.ReservationNotFound(id));
    }

    public ReservationDTO createReservation(ReservationDTO reservationDTO, Long userId) {
        User guest = userService.getUserEntityById(userId);
        User host = userService.getUserEntityById(reservationDTO.getHostId());
        Room room = roomService.findRoomEntityById(reservationDTO.getRoomId());
        
        // Проверяем, что пользователь не бронирует свою же комнату
        if (room.getHost() != null && room.getHost().getId().equals(userId)) {
            throw ReservationException.ReservationNotFound(userId);
        }

        Reservation reservation = convertToEntity(reservationDTO);
        reservation.setGuest(guest);
        reservation.setHost(host);
        reservation.setRoom(room);
        
        reservationRepository.save(reservation);
        return convertToDTO(reservation);
    }

    public ReservationDTO updateReservation(Long reservationId, ReservationDTO reservationDTO) {
        Reservation target = findReservationEntityById(reservationId);
        CopyUtils.copyNonNullProperties(reservationDTO, target);
        reservationRepository.save(target);
        return convertToDTO(target);
    }

    public ReservationDTO deleteReservation(Long reservationId) {
        Reservation target = findReservationEntityById(reservationId);
        reservationRepository.delete(target);
        return convertToDTO(target);
    }

    public Reservation convertToEntity(ReservationDTO reservationDTO) {
        Reservation reservation = new Reservation();
        BeanUtils.copyProperties(reservationDTO, reservation);
        
        // Конвертируем ZonedDateTime в Instant
        if (reservationDTO.getDateStart() != null) {
            reservation.setStartDate(reservationDTO.getDateStart().toInstant());
        }
        if (reservationDTO.getDateEnd() != null) {
            reservation.setEndDate(reservationDTO.getDateEnd().toInstant());
        }
        
        return reservation;
    }

    public ReservationDTO convertToDTO(Reservation reservation) {
        ReservationDTO reservationDTO = new ReservationDTO();
        BeanUtils.copyProperties(reservation, reservationDTO);
        
        // Устанавливаем ID комнаты, гостя и хоста
        if (reservation.getRoom() != null) {
            reservationDTO.setRoomId(reservation.getRoom().getId());
        }
        if (reservation.getGuest() != null) {
            reservationDTO.setUserId(reservation.getGuest().getId());
        }
        if (reservation.getHost() != null) {
            reservationDTO.setHostId(reservation.getHost().getId());
        }
        
        // Конвертируем Instant в ZonedDateTime
        if (reservation.getStartDate() != null) {
            reservationDTO.setDateStart(reservation.getStartDate().atZone(ZoneId.systemDefault()));
        }
        if (reservation.getEndDate() != null) {
            reservationDTO.setDateEnd(reservation.getEndDate().atZone(ZoneId.systemDefault()));
        }
        
        return reservationDTO;
    }
} 