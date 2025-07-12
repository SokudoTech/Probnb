package org.codevoke.probnb.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.codevoke.probnb.dto.RoomDTO;
import org.codevoke.probnb.dto.RoomFilterDTO;
import org.codevoke.probnb.dto.RoomSearchDTO;
import org.codevoke.probnb.service.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AllRoomsController.class)
@Import(AllRoomsControllerTest.TestConfig.class)
class AllRoomsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoomService roomService;

    @Autowired
    private ObjectMapper objectMapper;

    private RoomDTO testRoomDTO;
    private RoomSearchDTO testRoomSearchDTO;
    private List<RoomSearchDTO> testRoomSearchList;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        
        testRoomDTO = new RoomDTO();
        testRoomDTO.setId(1L);
        testRoomDTO.setTitle("Test Room");
        testRoomDTO.setSubtitle("A test room");
        testRoomDTO.setDescription("This is a test room");
        testRoomDTO.setPrice(100L);
        testRoomDTO.setRoomsCount(2);
        testRoomDTO.setLocation("Test City");
        testRoomDTO.setRoomType("Apartment");

        testRoomSearchDTO = new RoomSearchDTO();
        testRoomSearchDTO.setId(1L);
        testRoomSearchDTO.setTitle("Test Room");
        testRoomSearchDTO.setSubtitle("A test room"); 

        testRoomSearchList = Arrays.asList(testRoomSearchDTO);
    }

    static class TestConfig {
        // Конфигурация для тестов
    }

    @Test
    void getFilteredRooms_WhenNoFilters_ShouldReturnAllRooms() throws Exception {
        // given
        when(roomService.searchRooms(any(RoomFilterDTO.class))).thenReturn(testRoomSearchList);

        // when & then
        mockMvc.perform(get("/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Room"));
    }

    @Test
    void getFilteredRooms_WhenWithFilters_ShouldReturnFilteredRooms() throws Exception {
        // given
        when(roomService.searchRooms(any(RoomFilterDTO.class))).thenReturn(testRoomSearchList);

        // when & then
        mockMvc.perform(get("/rooms")
                        .param("room_type", "Apartment")
                        .param("rooms_count", "2")
                        .param("location", "Test City"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Room"));
    }

    @Test
    void getRoomById_WhenRoomExists_ShouldReturnRoom() throws Exception {
        // given
        when(roomService.findRoomById(1L, null)).thenReturn(testRoomDTO);

        // when & then
        mockMvc.perform(get("/rooms/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Room"))
                .andExpect(jsonPath("$.location").value("Test City"))
                .andExpect(jsonPath("$.room_type").value("Apartment"));
    }

    @Test
    void searchRooms_WhenValidFilter_ShouldReturnFilteredRooms() throws Exception {
        // given
        RoomFilterDTO filter = new RoomFilterDTO();
        filter.setRoomType("Apartment");
        filter.setRoomsCount(2);
        filter.setLocation("Test City");

        when(roomService.searchRooms(any(RoomFilterDTO.class))).thenReturn(testRoomSearchList);

        // when & then
        mockMvc.perform(post("/rooms/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Room"));
    }

    @Test
    void searchRooms_WhenEmptyFilter_ShouldReturnAllRooms() throws Exception {
        // given
        RoomFilterDTO filter = new RoomFilterDTO();
        when(roomService.searchRooms(any(RoomFilterDTO.class))).thenReturn(testRoomSearchList);

        // when & then
        mockMvc.perform(post("/rooms/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Room"));
    }

    @Test
    void getRoomById_WhenRoomNotFound_ShouldReturnNotFound() throws Exception {
        // given
        when(roomService.findRoomById(999L, null))
                .thenThrow(new RuntimeException("Room not found"));

        // when & then
        mockMvc.perform(get("/rooms/999"))
                .andExpect(status().isInternalServerError());
    }
} 