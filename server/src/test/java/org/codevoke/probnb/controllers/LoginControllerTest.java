package org.codevoke.probnb.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.codevoke.probnb.dto.LoginDTO;
import org.codevoke.probnb.service.LoginService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LoginService loginService;

    private LoginController loginController;

    private ObjectMapper objectMapper;

    private LoginDTO testLoginDTO;
    private Map<String, String> testTokens;

    @BeforeEach
    void setUp() {
        loginController = new LoginController(loginService);
        mockMvc = MockMvcBuilders.standaloneSetup(loginController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // Поддержка ZonedDateTime
        
        testLoginDTO = new LoginDTO();
        testLoginDTO.setEmail("test@test.com");
        testLoginDTO.setPassword("password123");

        testTokens = new HashMap<>();
        testTokens.put("access_token", "test-access-token");
        testTokens.put("refresh_token", "test-refresh-token");
    }

    @Test
    void login_WhenValidCredentials_ShouldReturnTokens() throws Exception {
        // given
        Map<String, String> expectedResponse = Map.of(
                "access_token", "access_token_123",
                "refresh_token", "refresh_token_456"
        );
        when(loginService.login(any(LoginDTO.class))).thenReturn(expectedResponse);

        // when & then
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testLoginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value("access_token_123"))
                .andExpect(jsonPath("$.refresh_token").value("refresh_token_456"));
    }

    @Test
    void login_WhenInvalidEmail_ShouldReturnBadRequest() throws Exception {
        // given
        testLoginDTO.setEmail("invalid-email");

        // when & then
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testLoginDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_WhenEmptyEmail_ShouldReturnBadRequest() throws Exception {
        // given
        testLoginDTO.setEmail("");

        // when & then
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testLoginDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_WhenEmptyPassword_ShouldReturnBadRequest() throws Exception {
        // given
        testLoginDTO.setPassword("");

        // when & then
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testLoginDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_WhenPasswordTooShort_ShouldReturnBadRequest() throws Exception {
        // given
        testLoginDTO.setPassword("123");

        // when & then
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testLoginDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_WhenMissingEmail_ShouldReturnBadRequest() throws Exception {
        // given
        testLoginDTO.setEmail(null);

        // when & then
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testLoginDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_WhenMissingPassword_ShouldReturnBadRequest() throws Exception {
        // given
        testLoginDTO.setPassword(null);

        // when & then
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testLoginDTO)))
                .andExpect(status().isBadRequest());
    }
} 