package org.codevoke.probnb.integration;

import org.codevoke.probnb.dto.LoginDTO;
import org.codevoke.probnb.dto.RegisterDTO;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("probnb_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.show-sql", () -> "false");
    }

    @AfterAll
    static void tearDown() {
        if (postgres != null && postgres.isRunning()) {
            postgres.stop();
        }
    }

    @Autowired
    private TestRestTemplate restTemplate;


    @Test
    void whenRegisterAndLogin_thenShouldReturnTokens() {
        // given
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername("authuser");
        registerDTO.setFirstname("Auth");
        registerDTO.setLastname("User");
        registerDTO.setEmail("auth@test.com");
        registerDTO.setPassword("password123");

        // when - register
        ResponseEntity<RegisterDTO> registerResponse = restTemplate.postForEntity(
                "/api/register",
                registerDTO,
                RegisterDTO.class);

        // then - register successful
        assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // when - login
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("auth@test.com");
        loginDTO.setPassword("password123");

        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> loginResponse = restTemplate.postForEntity(
                "/api/login",
                loginDTO,
                Map.class);

        // then - login successful
        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        @SuppressWarnings("unchecked")
        Map<String, String> tokens = (Map<String, String>) loginResponse.getBody();
        assertThat(tokens).isNotNull();
        assertThat(tokens).containsKey("access_token");
        assertThat(tokens).containsKey("refresh_token");
        assertThat(tokens.get("access_token")).isNotNull();
        assertThat(tokens.get("refresh_token")).isNotNull();
    }

    @Test
    void whenLoginWithWrongPassword_thenShouldReturnBadRequest() {
        // given - register user first
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername("wrongpassuser");
        registerDTO.setFirstname("Wrong");
        registerDTO.setLastname("Password");
        registerDTO.setEmail("wrongpass@test.com");
        registerDTO.setPassword("correctpassword");

        restTemplate.postForEntity("/api/register", registerDTO, RegisterDTO.class);

        // when - login with wrong password
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("wrongpass@test.com");
        loginDTO.setPassword("wrongpassword");

        ResponseEntity<Object> response = restTemplate.postForEntity(
                "/api/login",
                loginDTO,
                Object.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void whenLoginWithNonExistentEmail_thenShouldReturnBadRequest() {
        // given
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("nonexistent@test.com");
        loginDTO.setPassword("password123");

        // when
        ResponseEntity<Object> response = restTemplate.postForEntity(
                "/api/login",
                loginDTO,
                Object.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void whenLoginWithInvalidEmail_thenShouldReturnBadRequest() {
        // given
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("invalid-email");
        loginDTO.setPassword("password123");

        // when
        ResponseEntity<Object> response = restTemplate.postForEntity(
                "/api/login",
                loginDTO,
                Object.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void whenLoginWithEmptyPassword_thenShouldReturnBadRequest() {
        // given
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("test@test.com");
        loginDTO.setPassword("");

        // when
        ResponseEntity<Object> response = restTemplate.postForEntity(
                "/api/login",
                loginDTO,
                Object.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void whenLoginWithShortPassword_thenShouldReturnBadRequest() {
        // given
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("test@test.com");
        loginDTO.setPassword("123");

        // when
        ResponseEntity<Object> response = restTemplate.postForEntity(
                "/api/login",
                loginDTO,
                Object.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
} 