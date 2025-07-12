package org.codevoke.probnb.utils;

import io.jsonwebtoken.Claims;
import org.codevoke.probnb.model.Auth;
import org.codevoke.probnb.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private JwtService jwtService;
    private Auth testAuth;
    private User testUser;

    @BeforeEach
    void setUp() {
        // Используем тестовые секреты
        String testAccessSecret = "qBTmv4oXFFR2GwjexDJ4t6fsIUIUhhXqlktXjXdkcyygs8nPVEwMfo29VDRRepYDVV5IkIxBMzr7OEHXEHd37w==";
        String testRefreshSecret = "zL1HB3Pch05Avfynovxrf/kpF9O2m4NCWKJUjEp27s9J2jEG3ifiKCGylaZ8fDeoONSTJP/wAzKawB8F9rOMNg==";
        
        jwtService = new JwtService(testAccessSecret, testRefreshSecret);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testAuth = new Auth();
        testAuth.setId(1L);
        testAuth.setEmail("test@example.com");
        testAuth.setUser(testUser);
    }

    @Test
    void generateAccessToken_ShouldReturnValidToken() {
        // when
        String token = jwtService.generateAccessToken(testAuth, testUser);

        // then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(jwtService.validateAccessToken(token)).isTrue();
    }

    @Test
    void generateRefreshToken_ShouldReturnValidToken() {
        // when
        String token = jwtService.generateRefreshToken(testAuth);

        // then
        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(jwtService.validateRefreshToken(token)).isTrue();
    }

    @Test
    void validateAccessToken_WhenValidToken_ShouldReturnTrue() {
        // given
        String token = jwtService.generateAccessToken(testAuth, testUser);

        // when
        boolean isValid = jwtService.validateAccessToken(token);

        // then
        assertThat(isValid).isTrue();
    }

    @Test
    void validateAccessToken_WhenInvalidToken_ShouldReturnFalse() {
        // given
        String invalidToken = "invalid.token.here";

        // when
        boolean isValid = jwtService.validateAccessToken(invalidToken);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    void validateRefreshToken_WhenValidToken_ShouldReturnTrue() {
        // given
        String token = jwtService.generateRefreshToken(testAuth);

        // when
        boolean isValid = jwtService.validateRefreshToken(token);

        // then
        assertThat(isValid).isTrue();
    }

    @Test
    void validateRefreshToken_WhenInvalidToken_ShouldReturnFalse() {
        // given
        String invalidToken = "invalid.token.here";

        // when
        boolean isValid = jwtService.validateRefreshToken(invalidToken);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    void getAccessClaims_WhenValidToken_ShouldReturnClaims() {
        // given
        String token = jwtService.generateAccessToken(testAuth, testUser);

        // when
        Claims claims = jwtService.getAccessClaims(token);

        // then
        assertThat(claims).isNotNull();
        assertThat(claims.getSubject()).isEqualTo("test@example.com");
        assertThat(claims.get("user_id")).isEqualTo(1L);
    }

    @Test
    void getRefreshClaims_WhenValidToken_ShouldReturnClaims() {
        // given
        String token = jwtService.generateRefreshToken(testAuth);

        // when
        Claims claims = jwtService.getRefreshClaims(token);

        // then
        assertThat(claims).isNotNull();
        assertThat(claims.getSubject()).isEqualTo("test@example.com");
    }

    @Test
    void getAccessClaims_WhenInvalidToken_ShouldThrowException() {
        // given
        String invalidToken = "invalid.token.here";

        // when & then
        assertThatThrownBy(() -> jwtService.getAccessClaims(invalidToken))
                .isInstanceOf(Exception.class);
    }

    @Test
    void accessToken_ShouldExpireAfter5Minutes() {
        // given
        String token = jwtService.generateAccessToken(testAuth, testUser);

        // when
        Claims claims = jwtService.getAccessClaims(token);
        LocalDateTime expiration = claims.getExpiration().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime now = LocalDateTime.now();

        // then
        assertThat(expiration).isAfter(now);
        assertThat(expiration).isBefore(now.plusMinutes(6));
    }

    @Test
    void refreshToken_ShouldExpireAfter30Days() {
        // given
        String token = jwtService.generateRefreshToken(testAuth);

        // when
        Claims claims = jwtService.getRefreshClaims(token);
        LocalDateTime expiration = claims.getExpiration().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime now = LocalDateTime.now();

        // then
        assertThat(expiration).isAfter(now);
        assertThat(expiration).isBefore(now.plusDays(31));
    }
} 