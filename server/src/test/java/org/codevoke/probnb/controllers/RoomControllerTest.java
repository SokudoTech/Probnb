package org.codevoke.probnb.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.codevoke.probnb.dto.RoomDTO;
import org.codevoke.probnb.service.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class RoomControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RoomService roomService;

    private RoomController roomController;

    private ObjectMapper objectMapper;

    private RoomDTO testRoomDTO;
    private List<RoomDTO> testRoomList;

    @BeforeEach
    void setUp() {
        roomController = new RoomController(roomService);
        mockMvc = MockMvcBuilders.standaloneSetup(roomController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // Поддержка ZonedDateTime
        
        testRoomDTO = new RoomDTO();
        testRoomDTO.setId(1L);
        testRoomDTO.setTitle("Test Room");
        testRoomDTO.setSubtitle("A test room");
        testRoomDTO.setDescription("This is a test room");
        testRoomDTO.setPrice(100L);
        testRoomDTO.setRoomsCount(2);
        testRoomDTO.setLocation("Test City");
        testRoomDTO.setRoomType("Apartment");

        testRoomList = Arrays.asList(testRoomDTO);
    }

    @Test
    void getRoomById_WhenRoomExists_ShouldReturnRoom() throws Exception {
        // given
        when(roomService.findRoomById(1L, 1L)).thenReturn(testRoomDTO);

        // when & then
        mockMvc.perform(get("/users/1/rooms/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Room"))
                .andExpect(jsonPath("$.location").value("Test City"))
                .andExpect(jsonPath("$.room_type").value("Apartment"));
    }

    @Test
    void getRoomsByHostId_WhenRoomsExist_ShouldReturnRoomList() throws Exception {
        // given
        when(roomService.findRoomsByHostId(1L)).thenReturn(testRoomList);

        // when & then
        mockMvc.perform(get("/users/1/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Room"))
                .andExpect(jsonPath("$[0].location").value("Test City"));
    }

    @Test
    void createRoom_WhenValidData_ShouldReturnCreatedRoom() throws Exception {
        // given
        RoomDTO createDTO = new RoomDTO();
        createDTO.setTitle("New Room");
        createDTO.setSubtitle("A new room");
        createDTO.setDescription("This is a new room");
        createDTO.setPrice(150L);
        createDTO.setRoomsCount(3);
        createDTO.setLocation("New City");
        createDTO.setRoomType("House");

        when(roomService.createRoom(any(RoomDTO.class), eq(1L))).thenReturn(testRoomDTO);

        // when & then
        mockMvc.perform(post("/users/1/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Room"));
    }

    @Test
    void createRoom_WhenInvalidData_ShouldReturnBadRequest() throws Exception {
        // given
        RoomDTO invalidDTO = new RoomDTO();
        // Missing required fields

        // when & then
        mockMvc.perform(post("/api/users/1/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateRoom_WhenValidData_ShouldReturnUpdatedRoom() throws Exception {
        // given
        RoomDTO updateDTO = new RoomDTO();
        updateDTO.setTitle("Updated Room");

        when(roomService.patchRoom(eq(1L), any(RoomDTO.class))).thenReturn(testRoomDTO);

        // when & then
        mockMvc.perform(patch("/api/users/1/rooms/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Room"));
    }

    @Test
    void updateRoom_WhenRoomNotFound_ShouldReturnNotFound() throws Exception {
        // given
        RoomDTO updateDTO = new RoomDTO();
        updateDTO.setTitle("Updated Room");

        when(roomService.patchRoom(eq(999L), any(RoomDTO.class)))
                .thenThrow(new RuntimeException("Room not found"));

        // when & then
        mockMvc.perform(patch("/api/users/1/rooms/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void createRoom_WhenTitleTooShort_ShouldReturnBadRequest() throws Exception {
        // given
        RoomDTO createDTO = new RoomDTO();
        createDTO.setTitle("A"); // Too short
        createDTO.setSubtitle("A new room");
        createDTO.setDescription("This is a new room");
        createDTO.setPrice(150L);
        createDTO.setRoomsCount(3);
        createDTO.setLocation("New City");
        createDTO.setRoomType("House");

        // when & then
        mockMvc.perform(post("/api/users/1/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRoom_WhenTitleTooLong_ShouldReturnBadRequest() throws Exception {
        // given
        RoomDTO createDTO = new RoomDTO();
        createDTO.setTitle("A".repeat(51)); // Too long
        createDTO.setSubtitle("A new room");
        createDTO.setDescription("This is a new room");
        createDTO.setPrice(150L);
        createDTO.setRoomsCount(3);
        createDTO.setLocation("New City");
        createDTO.setRoomType("House");

        // when & then
        mockMvc.perform(post("/api/users/1/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRoom_WhenDescriptionTooLong_ShouldReturnBadRequest() throws Exception {
        // given
        RoomDTO createDTO = new RoomDTO();
        createDTO.setTitle("Valid Title");
        createDTO.setSubtitle("A new room");
        createDTO.setDescription("A".repeat(1001)); // Too long
        createDTO.setPrice(150L);
        createDTO.setRoomsCount(3);
        createDTO.setLocation("New City");
        createDTO.setRoomType("House");

        // when & then
        mockMvc.perform(post("/api/users/1/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRoom_WhenLocationTooLong_ShouldReturnBadRequest() throws Exception {
        // given
        RoomDTO createDTO = new RoomDTO();
        createDTO.setTitle("Valid Title");
        createDTO.setSubtitle("A new room");
        createDTO.setDescription("This is a new room");
        createDTO.setPrice(150L);
        createDTO.setRoomsCount(3);
        createDTO.setLocation("A".repeat(101)); // Too long
        createDTO.setRoomType("House");

        // when & then
        mockMvc.perform(post("/api/users/1/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isBadRequest());
    }
} 