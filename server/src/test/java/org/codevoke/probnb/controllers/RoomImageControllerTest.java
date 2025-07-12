package org.codevoke.probnb.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.codevoke.probnb.dto.RoomImageDTO;
import org.codevoke.probnb.service.RoomImageService;
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
class RoomImageControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RoomImageService roomImageService;

    private RoomImageController roomImageController;

    private ObjectMapper objectMapper;

    private RoomImageDTO testRoomImageDTO;
    private List<RoomImageDTO> testRoomImageList;

    @BeforeEach
    void setUp() {
        roomImageController = new RoomImageController(roomImageService);
        mockMvc = MockMvcBuilders.standaloneSetup(roomImageController).build();
        objectMapper = new ObjectMapper();
        
        testRoomImageDTO = new RoomImageDTO();
        testRoomImageDTO.setId(1L);
        testRoomImageDTO.setRoomId(1L);
        testRoomImageDTO.setImageId(2L);

        testRoomImageList = Arrays.asList(testRoomImageDTO);
    }

    @Test
    void getRoomImageById_WhenRoomImageExists_ShouldReturnRoomImage() throws Exception {
        // given
        when(roomImageService.findRoomImageById(1L)).thenReturn(testRoomImageDTO);

        // when & then
        mockMvc.perform(get("/api/rooms/1/images/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.room_id").value(1))
                .andExpect(jsonPath("$.image_id").value(2));
    }

    @Test
    void getRoomImagesByRoomId_WhenRoomImagesExist_ShouldReturnRoomImageList() throws Exception {
        // given
        when(roomImageService.findRoomImagesByRoomId(1L)).thenReturn(testRoomImageList);

        // when & then
        mockMvc.perform(get("/api/rooms/1/images"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].room_id").value(1))
                .andExpect(jsonPath("$[0].image_id").value(2));
    }

    @Test
    void createRoomImage_WhenValidData_ShouldReturnCreatedRoomImage() throws Exception {
        // given
        RoomImageDTO createDTO = new RoomImageDTO();
        createDTO.setRoomId(1L);
        createDTO.setImageId(2L);

        when(roomImageService.createRoomImage(any(RoomImageDTO.class))).thenReturn(testRoomImageDTO);

        // when & then
        mockMvc.perform(post("/api/rooms/1/images")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.room_id").value(1))
                .andExpect(jsonPath("$.image_id").value(2));
    }

    @Test
    void createRoomImage_WhenInvalidData_ShouldReturnBadRequest() throws Exception {
        // given
        RoomImageDTO invalidDTO = new RoomImageDTO();
        // Missing required fields

        // when & then
        mockMvc.perform(post("/api/rooms/1/images")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateRoomImage_WhenValidData_ShouldReturnUpdatedRoomImage() throws Exception {
        // given
        RoomImageDTO updateDTO = new RoomImageDTO();
        updateDTO.setImageId(3L);

        when(roomImageService.updateRoomImage(eq(1L), any(RoomImageDTO.class))).thenReturn(testRoomImageDTO);

        // when & then
        mockMvc.perform(patch("/api/rooms/1/images/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void createRoomImage_WhenMissingRoomId_ShouldReturnBadRequest() throws Exception {
        // given
        RoomImageDTO missingRoomIdDTO = new RoomImageDTO();
        missingRoomIdDTO.setImageId(2L);
        // Missing roomId

        // when & then
        mockMvc.perform(post("/api/rooms/1/images")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(missingRoomIdDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRoomImage_WhenMissingImageId_ShouldReturnBadRequest() throws Exception {
        // given
        RoomImageDTO missingImageIdDTO = new RoomImageDTO();
        missingImageIdDTO.setRoomId(1L);
        // Missing imageId

        // when & then
        mockMvc.perform(post("/api/rooms/1/images")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(missingImageIdDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRoomImage_WhenNullRoomId_ShouldReturnBadRequest() throws Exception {
        // given
        RoomImageDTO nullRoomIdDTO = new RoomImageDTO();
        nullRoomIdDTO.setRoomId(null);
        nullRoomIdDTO.setImageId(2L);

        // when & then
        mockMvc.perform(post("/api/rooms/1/images")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nullRoomIdDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRoomImage_WhenNullImageId_ShouldReturnBadRequest() throws Exception {
        // given
        RoomImageDTO nullImageIdDTO = new RoomImageDTO();
        nullImageIdDTO.setRoomId(1L);
        nullImageIdDTO.setImageId(null);

        // when & then
        mockMvc.perform(post("/api/rooms/1/images")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nullImageIdDTO)))
                .andExpect(status().isBadRequest());
    }
} 