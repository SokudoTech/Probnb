package org.codevoke.probnb.services;

import org.codevoke.probnb.dto.LoginDTO;
import org.codevoke.probnb.exceptions.UserException;
import org.codevoke.probnb.model.Auth;
import org.codevoke.probnb.model.User;
import org.codevoke.probnb.repository.AuthRepository;
import org.codevoke.probnb.service.LoginService;
import org.codevoke.probnb.utils.JwtService;
import org.codevoke.probnb.utils.PasswordHasher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    private AuthRepository authRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private LoginService loginService;

    private Auth testAuth;
    private User testUser;
    private LoginDTO testLoginDTO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setFirstname("Test");
        testUser.setLastname("User");

        testAuth = new Auth();
        testAuth.setId(1L);
        testAuth.setEmail("test@example.com");
        testAuth.setPassword(PasswordHasher.hash("password123"));
        testAuth.setUser(testUser);

        testLoginDTO = new LoginDTO();
        testLoginDTO.setEmail("test@example.com");
        testLoginDTO.setPassword("password123");
    }

    @Test
    void login_WhenValidCredentials_ShouldReturnTokens() {
        // given
        when(authRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testAuth));
        when(jwtService.generateAccessToken(any(Auth.class), any(User.class)))
                .thenReturn("access_token_123");
        when(jwtService.generateRefreshToken(any(Auth.class)))
                .thenReturn("refresh_token_456");

        // when
        Map<String, String> result = loginService.login(testLoginDTO);

        // then
        assertThat(result).isNotNull();
        assertThat(result).containsKey("access_token");
        assertThat(result).containsKey("refresh_token");
        assertThat(result.get("access_token")).isEqualTo("access_token_123");
        assertThat(result.get("refresh_token")).isEqualTo("refresh_token_456");
    }

    @Test
    void login_WhenUserNotFound_ShouldThrowUserException() {
        // given
        when(authRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());
        testLoginDTO.setEmail("nonexistent@example.com");

        // when & then
        assertThatThrownBy(() -> loginService.login(testLoginDTO))
                .isInstanceOf(UserException.class)
                .hasMessageContaining("Login or password is incorrect");
    }

    @Test
    void login_WhenWrongPassword_ShouldThrowUserException() {
        // given
        when(authRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testAuth));
        testLoginDTO.setPassword("wrongpassword");

        // when & then
        assertThatThrownBy(() -> loginService.login(testLoginDTO))
                .isInstanceOf(UserException.class)
                .hasMessageContaining("Login or password is incorrect");
    }

    @Test
    void login_WhenEmptyEmail_ShouldThrowUserException() {
        // given
        when(authRepository.findByEmail("")).thenReturn(Optional.empty());
        testLoginDTO.setEmail("");

        // when & then
        assertThatThrownBy(() -> loginService.login(testLoginDTO))
                .isInstanceOf(UserException.class)
                .hasMessageContaining("Login or password is incorrect");
    }
} 