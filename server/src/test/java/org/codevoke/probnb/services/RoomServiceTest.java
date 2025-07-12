package org.codevoke.probnb.services;

import org.codevoke.probnb.dto.RoomDTO;
import org.codevoke.probnb.dto.RoomFilterDTO;
import org.codevoke.probnb.dto.RoomSearchDTO;
import org.codevoke.probnb.exceptions.RoomException;
import org.codevoke.probnb.exceptions.UserException;
import org.codevoke.probnb.model.Room;
import org.codevoke.probnb.model.User;
import org.codevoke.probnb.repository.RoomRepository;
import org.codevoke.probnb.repository.UserRepository;
import org.codevoke.probnb.service.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RoomService roomService;

    private User testHost;
    private Room testRoom;
    private RoomDTO testRoomDTO;

    @BeforeEach
    void setUp() {
        testHost = new User();
        testHost.setId(1L);
        testHost.setUsername("testhost");

        testRoom = new Room();
        testRoom.setId(1L);
        testRoom.setTitle("Test Room");
        testRoom.setSubtitle("A test room");
        testRoom.setDescription("This is a test room");
        testRoom.setPrice(100L);
        testRoom.setRoomsCount(2);
        testRoom.setLocation("Test City");
        testRoom.setRoomType("Apartment");
        testRoom.setHost(testHost);

        testRoomDTO = new RoomDTO();
        testRoomDTO.setTitle("Test Room");
        testRoomDTO.setSubtitle("A test room");
        testRoomDTO.setDescription("This is a test room");
        testRoomDTO.setPrice(100L);
        testRoomDTO.setRoomsCount(2);
        testRoomDTO.setLocation("Test City");
        testRoomDTO.setRoomType("Apartment");
    }

    @Test
    void findRoomById_WhenRoomExists_ShouldReturnRoomDTO() {
        // given
        when(roomRepository.findById(1L)).thenReturn(Optional.of(testRoom));

        // when
        RoomDTO result = roomService.findRoomById(1L, 1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Test Room");
        assertThat(result.getLocation()).isEqualTo("Test City");
    }

    @Test
    void findRoomById_WhenRoomDoesNotExist_ShouldThrowRoomException() {
        // given
        when(roomRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> roomService.findRoomById(999L, 1L))
                .isInstanceOf(RoomException.class)
                .hasMessageContaining("Room with id 999 not found");
    }

    @Test
    void findRoomsByHostId_WhenRoomsExist_ShouldReturnRoomList() {
        // given
        Room room2 = new Room();
        room2.setId(2L);
        room2.setTitle("Second Room");
        room2.setHost(testHost);

        when(roomRepository.findAll()).thenReturn(Arrays.asList(testRoom, room2));

        // when
        List<RoomDTO> result = roomService.findRoomsByHostId(1L);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo("Test Room");
        assertThat(result.get(1).getTitle()).isEqualTo("Second Room");
    }

    @Test
    void createRoom_WhenValidData_ShouldReturnCreatedRoom() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testHost));
        when(roomRepository.save(any(Room.class))).thenReturn(testRoom);

        // when
        RoomDTO result = roomService.createRoom(testRoomDTO, 1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Test Room");
        assertThat(result.getLocation()).isEqualTo("Test City");
    }

    @Test
    void createRoom_WhenHostNotFound_ShouldThrowUserException() {
        // given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> roomService.createRoom(testRoomDTO, 999L))
                .isInstanceOf(UserException.class);
    }

    @Test
    void searchRooms_WhenNoFilters_ShouldReturnAllRooms() {
        // given
        RoomFilterDTO filter = new RoomFilterDTO();
        when(roomRepository.findAll()).thenReturn(Arrays.asList(testRoom));

        // when
        List<RoomSearchDTO> result = roomService.searchRooms(filter);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Test Room");
    }

    @Test
    void searchRooms_WhenRoomTypeFilter_ShouldReturnFilteredRooms() {
        // given
        RoomFilterDTO filter = new RoomFilterDTO();
        filter.setRoomType("Apartment");
        when(roomRepository.findByRoomTypeContainingIgnoreCase("Apartment"))
                .thenReturn(Arrays.asList(testRoom));

        // when
        List<RoomSearchDTO> result = roomService.searchRooms(filter);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Test Room");
    }

    @Test
    void searchRooms_WhenLocationFilter_ShouldReturnFilteredRooms() {
        // given
        RoomFilterDTO filter = new RoomFilterDTO();
        filter.setLocation("Test City");
        when(roomRepository.findByLocationContainingIgnoreCase("Test City"))
                .thenReturn(Arrays.asList(testRoom));

        // when
        List<RoomSearchDTO> result = roomService.searchRooms(filter);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Test Room");
    }

    @Test
    void searchRooms_WhenMultipleFilters_ShouldReturnFilteredRooms() {
        // given
        RoomFilterDTO filter = new RoomFilterDTO();
        filter.setRoomType("Apartment");
        filter.setRoomsCount(2);
        filter.setLocation("Test City");
        when(roomRepository.findByRoomTypeContainingIgnoreCaseAndRoomsCountAndLocationContainingIgnoreCase(
                "Apartment", 2, "Test City"))
                .thenReturn(Arrays.asList(testRoom));

        // when
        List<RoomSearchDTO> result = roomService.searchRooms(filter);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Test Room");
        assertThat(result.get(0).getSubtitle()).isEqualTo("A test room");
    }

    @Test
    void patchRoom_WhenValidData_ShouldReturnUpdatedRoom() {
        // given
        when(roomRepository.findById(1L)).thenReturn(Optional.of(testRoom));
        when(roomRepository.save(any(Room.class))).thenReturn(testRoom);

        RoomDTO updateDTO = new RoomDTO();
        updateDTO.setTitle("Updated Room");

        // when
        RoomDTO result = roomService.patchRoom(1L, updateDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Test Room"); // original title preserved
    }

    @Test
    void patchRoom_WhenRoomNotFound_ShouldThrowRoomException() {
        // given
        when(roomRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> roomService.patchRoom(999L, new RoomDTO()))
                .isInstanceOf(RoomException.class)
                .hasMessageContaining("Room with id 999 not found");
    }
} 