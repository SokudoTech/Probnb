package org.codevoke.probnb.controllers;

import org.codevoke.probnb.dto.UserDTO;
import org.codevoke.probnb.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    private UserController userController;

    private UserDTO testUserDTO;

    @BeforeEach
    void setUp() {
        userController = new UserController(userService);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        
        testUserDTO = new UserDTO();
        testUserDTO.setId(1L);
        testUserDTO.setUsername("testuser");
        testUserDTO.setFirstname("Test");
        testUserDTO.setLastname("User");
        testUserDTO.setAvatar(2L);
    }

    @Test
    void getUser_WhenUserExists_ShouldReturnUser() throws Exception {
        // given
        when(userService.getUserById(1L)).thenReturn(testUserDTO);

        // when & then
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.firstname").value("Test"))
                .andExpect(jsonPath("$.lastname").value("User"))
                .andExpect(jsonPath("$.avatar").value(2));
    }

    @Test
    void getUser_WhenUserDoesNotExist_ShouldReturnNotFound() throws Exception {
        // given
        when(userService.getUserById(999L))
                .thenThrow(new RuntimeException("User not found"));

        // when & then
        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getUser_WhenUserWithoutAvatar_ShouldReturnUserWithoutAvatar() throws Exception {
        // given
        testUserDTO.setAvatar(null);
        when(userService.getUserById(1L)).thenReturn(testUserDTO);

        // when & then
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.firstname").value("Test"))
                .andExpect(jsonPath("$.lastname").value("User"))
                .andExpect(jsonPath("$.avatar").doesNotExist());
    }

    @Test
    void getUser_WhenUserWithEmptyLastname_ShouldReturnUserWithEmptyLastname() throws Exception {
        // given
        testUserDTO.setLastname("");
        when(userService.getUserById(1L)).thenReturn(testUserDTO);

        // when & then
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.firstname").value("Test"))
                .andExpect(jsonPath("$.lastname").value(""));
    }

    @Test
    void getUser_WhenUserWithNullLastname_ShouldReturnUserWithNullLastname() throws Exception {
        // given
        testUserDTO.setLastname(null);
        when(userService.getUserById(1L)).thenReturn(testUserDTO);

        // when & then
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.firstname").value("Test"))
                .andExpect(jsonPath("$.lastname").doesNotExist());
    }
} 