package org.codevoke.probnb.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.codevoke.probnb.dto.ImageDTO;
import org.codevoke.probnb.service.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ImageControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ImageService imageService;

    private ImageController imageController;

    private ObjectMapper objectMapper;

    private ImageDTO testImageDTO;
    private byte[] testImageBytes;

    @BeforeEach
    void setUp() {
        imageController = new ImageController(imageService);
        mockMvc = MockMvcBuilders.standaloneSetup(imageController).build();
        objectMapper = new ObjectMapper();
        
        testImageDTO = new ImageDTO();
        testImageDTO.setId(1L);
        testImageDTO.setImage("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==");

        testImageBytes = "test image bytes".getBytes();
    }

    @Test
    void getImage_WhenImageExists_ShouldReturnImageBytes() throws Exception {
        // given
        when(imageService.getImageById(1L)).thenReturn(testImageBytes);

        // when & then
        mockMvc.perform(get("/api/images/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG_VALUE))
                .andExpect(content().bytes(testImageBytes));
    }

    @Test
    void uploadImage_WhenValidData_ShouldReturnCreatedImage() throws Exception {
        // given
        ImageDTO createDTO = new ImageDTO();
        createDTO.setImage("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==");

        when(imageService.createImage(any(ImageDTO.class))).thenReturn(testImageDTO);

        // when & then
        mockMvc.perform(post("/api/images")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void uploadImage_WhenInvalidBase64Data_ShouldReturnBadRequest() throws Exception {
        // given
        ImageDTO invalidDTO = new ImageDTO();
        invalidDTO.setImage("invalid-base64-data");

        // when & then
        mockMvc.perform(post("/api/images")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void uploadImage_WhenEmptyImageData_ShouldReturnBadRequest() throws Exception {
        // given
        ImageDTO emptyDTO = new ImageDTO();
        emptyDTO.setImage("");

        // when & then
        mockMvc.perform(post("/api/images")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void uploadImage_WhenMissingImageData_ShouldReturnBadRequest() throws Exception {
        // given
        ImageDTO missingDTO = new ImageDTO();
        // Missing image field

        // when & then
        mockMvc.perform(post("/api/images")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(missingDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void uploadImage_WhenNullImageData_ShouldReturnBadRequest() throws Exception {
        // given
        ImageDTO nullDTO = new ImageDTO();
        nullDTO.setImage(null);

        // when & then
        mockMvc.perform(post("/api/images")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nullDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void uploadImage_WhenValidJpegData_ShouldReturnCreatedImage() throws Exception {
        // given
        ImageDTO jpegDTO = new ImageDTO();
        jpegDTO.setImage("data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEAYABgAAD/2wBDAAEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQH/2wBDAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQH/wAARCAABAAEDASIAAhEBAxEB/8QAFQABAQAAAAAAAAAAAAAAAAAAAAv/xAAUEAEAAAAAAAAAAAAAAAAAAAAA/8QAFQEBAQAAAAAAAAAAAAAAAAAAAAX/xAAUEQEAAAAAAAAAAAAAAAAAAAAA/9oADAMBAAIRAxEAPwA/8A");

        when(imageService.createImage(any(ImageDTO.class))).thenReturn(testImageDTO);

        // when & then
        mockMvc.perform(post("/api/images")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jpegDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void uploadImage_WhenValidGifData_ShouldReturnCreatedImage() throws Exception {
        // given
        ImageDTO gifDTO = new ImageDTO();
        gifDTO.setImage("data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7");

        when(imageService.createImage(any(ImageDTO.class))).thenReturn(testImageDTO);

        // when & then
        mockMvc.perform(post("/api/images")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gifDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }
} 