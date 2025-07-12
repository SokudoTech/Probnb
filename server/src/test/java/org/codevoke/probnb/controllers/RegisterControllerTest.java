package org.codevoke.probnb.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.codevoke.probnb.dto.RegisterDTO;
import org.codevoke.probnb.service.RegisterService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class RegisterControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RegisterService registerService;

    private RegisterController registerController;

    private ObjectMapper objectMapper;

    private RegisterDTO testRegisterDTO;
    private RegisterDTO testResponseDTO;

    @BeforeEach
    void setUp() {
        registerController = new RegisterController(registerService);
        mockMvc = MockMvcBuilders.standaloneSetup(registerController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // Поддержка ZonedDateTime
        
        testRegisterDTO = new RegisterDTO();
        testRegisterDTO.setUsername("testuser");
        testRegisterDTO.setFirstname("Test");
        testRegisterDTO.setLastname("User");
        testRegisterDTO.setEmail("test@test.com");
        testRegisterDTO.setPassword("password123");

        testResponseDTO = new RegisterDTO();
        testResponseDTO.setId(1L);
        testResponseDTO.setUsername("testuser");
        testResponseDTO.setFirstname("Test");
        testResponseDTO.setLastname("User");
        testResponseDTO.setEmail("test@example.com");
    }

    @Test
    void createUser_WhenValidData_ShouldReturnCreatedUser() throws Exception {
        // given
        when(registerService.register(any(RegisterDTO.class))).thenReturn(testResponseDTO);

        // when & then
        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRegisterDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.firstname").value("Test"))
                .andExpect(jsonPath("$.lastname").value("User"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void createUser_WhenInvalidEmail_ShouldReturnBadRequest() throws Exception {
        // given
        testRegisterDTO.setEmail("invalid-email");

        // when & then
        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRegisterDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_WhenEmptyUsername_ShouldReturnBadRequest() throws Exception {
        // given
        testRegisterDTO.setUsername("");

        // when & then
        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRegisterDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_WhenUsernameTooShort_ShouldReturnBadRequest() throws Exception {
        // given
        testRegisterDTO.setUsername("ab");

        // when & then
        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRegisterDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_WhenUsernameTooLong_ShouldReturnBadRequest() throws Exception {
        // given
        testRegisterDTO.setUsername("a".repeat(51));

        // when & then
        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRegisterDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_WhenEmptyFirstname_ShouldReturnBadRequest() throws Exception {
        // given
        testRegisterDTO.setFirstname("");

        // when & then
        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRegisterDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_WhenFirstnameTooLong_ShouldReturnBadRequest() throws Exception {
        // given
        testRegisterDTO.setFirstname("a".repeat(51));

        // when & then
        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRegisterDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_WhenLastnameTooLong_ShouldReturnBadRequest() throws Exception {
        // given
        testRegisterDTO.setLastname("a".repeat(51));

        // when & then
        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRegisterDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_WhenEmptyPassword_ShouldReturnBadRequest() throws Exception {
        // given
        testRegisterDTO.setPassword("");

        // when & then
        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRegisterDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_WhenPasswordTooShort_ShouldReturnBadRequest() throws Exception {
        // given
        testRegisterDTO.setPassword("123");

        // when & then
        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRegisterDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_WhenPasswordTooLong_ShouldReturnBadRequest() throws Exception {
        // given
        testRegisterDTO.setPassword("a".repeat(51));

        // when & then
        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRegisterDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_WhenEmailTooLong_ShouldReturnBadRequest() throws Exception {
        // given
        testRegisterDTO.setEmail("a".repeat(121) + "@example.com");

        // when & then
        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRegisterDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_WhenMissingRequiredFields_ShouldReturnBadRequest() throws Exception {
        // given
        RegisterDTO incompleteDTO = new RegisterDTO();
        incompleteDTO.setUsername("testuser");
        // Missing other required fields

        // when & then
        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incompleteDTO)))
                .andExpect(status().isBadRequest());
    }
} 