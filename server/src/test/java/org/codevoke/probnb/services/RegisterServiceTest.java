package org.codevoke.probnb.services;

import org.codevoke.probnb.dto.RegisterDTO;
import org.codevoke.probnb.exceptions.UserException;
import org.codevoke.probnb.exceptions.ServerException;
import org.codevoke.probnb.model.Auth;
import org.codevoke.probnb.model.User;
import org.codevoke.probnb.repository.AuthRepository;
import org.codevoke.probnb.repository.ImageRepository;
import org.codevoke.probnb.repository.UserRepository;
import org.codevoke.probnb.service.RegisterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterServiceTest {

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthRepository authRepository;

    @InjectMocks
    private RegisterService registerService;

    private RegisterDTO testRegisterDTO;

    @BeforeEach
    void setUp() {
        testRegisterDTO = new RegisterDTO();
        testRegisterDTO.setUsername("testuser");
        testRegisterDTO.setFirstname("Test");
        testRegisterDTO.setLastname("User");
        testRegisterDTO.setEmail("test@example.com");
        testRegisterDTO.setPassword("password123");
    }

    @Test
    void register_WhenValidData_ShouldReturnRegisterDTO() {
        // given
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("testuser");
        savedUser.setFirstname("Test");
        savedUser.setLastname("User");

        Auth savedAuth = new Auth();
        savedAuth.setId(1L);
        savedAuth.setEmail("test@example.com");
        savedAuth.setUser(savedUser);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(authRepository.save(any(Auth.class))).thenReturn(savedAuth);

        // when
        RegisterDTO result = registerService.register(testRegisterDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getFirstname()).isEqualTo("Test");
        assertThat(result.getLastname()).isEqualTo("User");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void register_WhenUsernameAlreadyExists_ShouldThrowUserException() {
        // given
        when(userRepository.save(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("user_username_unique"));

        // when & then
        assertThatThrownBy(() -> registerService.register(testRegisterDTO))
                .isInstanceOf(UserException.class)
                .hasMessageContaining("User with username testuser already exists");
    }

    @Test
    void register_WhenEmailAlreadyExists_ShouldThrowUserException() {
        // given
        when(userRepository.save(any(User.class))).thenReturn(new User());
        when(authRepository.save(any(Auth.class)))
                .thenThrow(new DataIntegrityViolationException("auth_email_unique"));

        // when & then
        assertThatThrownBy(() -> registerService.register(testRegisterDTO))
                .isInstanceOf(UserException.class)
                .hasMessageContaining("User with email test@example.com already exists");
    }

    @Test
    void register_WhenUnexpectedDatabaseError_ShouldThrowServerException() {
        // given
        when(userRepository.save(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("unexpected_error"));

        // when & then
        assertThatThrownBy(() -> registerService.register(testRegisterDTO))
                .isInstanceOf(ServerException.class)
                .hasMessageContaining("Server error: database query failed");
    }
} 