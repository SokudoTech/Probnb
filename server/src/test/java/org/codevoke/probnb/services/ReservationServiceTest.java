package org.codevoke.probnb.services;

import org.codevoke.probnb.dto.ReservationDTO;
import org.codevoke.probnb.exceptions.ReservationException;
import org.codevoke.probnb.model.Reservation;
import org.codevoke.probnb.model.Room;
import org.codevoke.probnb.model.User;
import org.codevoke.probnb.repository.ReservationRepository;
import org.codevoke.probnb.repository.RoomRepository;
import org.codevoke.probnb.repository.UserRepository;
import org.codevoke.probnb.service.ReservationService;
import org.codevoke.probnb.service.UserService;
import org.codevoke.probnb.service.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private UserService userService;

    @Mock
    private RoomService roomService;

    @InjectMocks
    private ReservationService reservationService;

    private User testGuest;
    private User testHost;
    private Room testRoom;
    private Reservation testReservation;
    private ReservationDTO testReservationDTO;

    @BeforeEach
    void setUp() {
        testGuest = new User();
        testGuest.setId(1L);
        testGuest.setUsername("guest");

        testHost = new User();
        testHost.setId(2L);
        testHost.setUsername("host");

        testRoom = new Room();
        testRoom.setId(1L);
        testRoom.setTitle("Test Room");
        testRoom.setHost(testHost);

        testReservation = new Reservation();
        testReservation.setId(1L);
        testReservation.setGuest(testGuest);
        testReservation.setHost(testHost);
        testReservation.setRoom(testRoom);
        testReservation.setStartDate(Instant.now().plusSeconds(3600)); // 1 hour from now
        testReservation.setEndDate(Instant.now().plusSeconds(7200)); // 2 hours from now

        testReservationDTO = new ReservationDTO();
        testReservationDTO.setRoomId(1L);
        testReservationDTO.setUserId(1L);
        testReservationDTO.setHostId(2L);
        testReservationDTO.setDateStart(ZonedDateTime.now().plusHours(1));
        testReservationDTO.setDateEnd(ZonedDateTime.now().plusHours(2));
    }

    @Test
    void findReservationById_WhenReservationExists_ShouldReturnReservationDTO() {
        // given
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));

        // when
        ReservationDTO result = reservationService.findReservationById(1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getRoomId()).isEqualTo(1L);
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getHostId()).isEqualTo(2L);
    }

    @Test
    void findReservationById_WhenReservationDoesNotExist_ShouldThrowReservationException() {
        // given
        when(reservationRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> reservationService.findReservationById(999L))
                .isInstanceOf(ReservationException.class)
                .hasMessageContaining("Reservation with id 999 not found");
    }

    @Test
    void findReservationsByUserId_WhenReservationsExist_ShouldReturnReservationList() {
        // given
        Reservation reservation2 = new Reservation();
        reservation2.setId(2L);
        reservation2.setGuest(testGuest);
        reservation2.setHost(testHost);
        reservation2.setRoom(testRoom);

        when(reservationRepository.findAll()).thenReturn(Arrays.asList(testReservation, reservation2));

        // when
        List<ReservationDTO> result = reservationService.findReservationsByUserId(1L);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(1).getId()).isEqualTo(2L);
    }

    @Test
    void createReservation_WhenValidData_ShouldReturnCreatedReservation() {
        // given
        when(userService.getUserEntityById(1L)).thenReturn(testGuest);
        when(userService.getUserEntityById(2L)).thenReturn(testHost);
        when(roomService.findRoomEntityById(1L)).thenReturn(testRoom);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);

        // when
        ReservationDTO result = reservationService.createReservation(testReservationDTO, 1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getRoomId()).isEqualTo(1L);
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getHostId()).isEqualTo(2L);
    }

    @Test
    void createReservation_WhenGuestBooksOwnRoom_ShouldThrowReservationException() {
        // given
        testRoom.setHost(testGuest); // Guest is the host of the room
        when(userService.getUserEntityById(1L)).thenReturn(testGuest);
        when(roomService.findRoomEntityById(1L)).thenReturn(testRoom);

        // when & then
        assertThatThrownBy(() -> reservationService.createReservation(testReservationDTO, 1L))
                .isInstanceOf(ReservationException.class);
    }

    @Test
    void updateReservation_WhenValidData_ShouldReturnUpdatedReservation() {
        // given
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(testReservation);

        ReservationDTO updateDTO = new ReservationDTO();
        updateDTO.setDateStart(ZonedDateTime.now().plusHours(3));
        updateDTO.setDateEnd(ZonedDateTime.now().plusHours(4));

        // when
        ReservationDTO result = reservationService.updateReservation(1L, updateDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void updateReservation_WhenReservationNotFound_ShouldThrowReservationException() {
        // given
        when(reservationRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> reservationService.updateReservation(999L, new ReservationDTO()))
                .isInstanceOf(ReservationException.class)
                .hasMessageContaining("Reservation with id 999 not found");
    }

    @Test
    void deleteReservation_WhenReservationExists_ShouldReturnDeletedReservation() {
        // given
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));

        // when
        ReservationDTO result = reservationService.deleteReservation(1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void deleteReservation_WhenReservationNotFound_ShouldThrowReservationException() {
        // given
        when(reservationRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> reservationService.deleteReservation(999L))
                .isInstanceOf(ReservationException.class)
                .hasMessageContaining("Reservation with id 999 not found");
    }

    @Test
    void convertToEntity_WhenValidDTO_ShouldReturnReservation() {
        // when
        Reservation result = reservationService.convertToEntity(testReservationDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStartDate()).isNotNull();
        assertThat(result.getEndDate()).isNotNull();
    }

    @Test
    void convertToDTO_WhenValidReservation_ShouldReturnReservationDTO() {
        // when
        ReservationDTO result = reservationService.convertToDTO(testReservation);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getRoomId()).isEqualTo(1L);
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getHostId()).isEqualTo(2L);
    }
} 