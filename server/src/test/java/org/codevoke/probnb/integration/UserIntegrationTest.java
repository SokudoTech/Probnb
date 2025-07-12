package org.codevoke.probnb.integration;

import org.codevoke.probnb.dto.RegisterDTO;
import org.codevoke.probnb.dto.UserDTO;
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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserIntegrationTest {

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
    void whenRegisterUser_thenGetUserReturnsCreatedUser() {
        // given
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername("integrationuser");
        registerDTO.setFirstname("Integration");
        registerDTO.setLastname("User");
        registerDTO.setEmail("integration@test.com");
        registerDTO.setPassword("password123");

        // when
        ResponseEntity<RegisterDTO> response = restTemplate.postForEntity(
                "/api/register",
                registerDTO,
                RegisterDTO.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        RegisterDTO createdUser = response.getBody();
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getId()).isNotNull();
        assertThat(createdUser.getUsername()).isEqualTo("integrationuser");
        assertThat(createdUser.getEmail()).isEqualTo("integration@test.com");

        // verify by GET
        ResponseEntity<UserDTO> getResponse = restTemplate.getForEntity(
                "/api/users/" + createdUser.getId(),
                UserDTO.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        UserDTO retrievedUser = getResponse.getBody();
        assertThat(retrievedUser).isNotNull();
        assertThat(retrievedUser.getUsername()).isEqualTo("integrationuser");
        assertThat(retrievedUser.getFirstname()).isEqualTo("Integration");
        assertThat(retrievedUser.getLastname()).isEqualTo("User");
    }

    @Test
    void whenRegisterUserWithDuplicateUsername_thenReturnConflict() {
        // given
        RegisterDTO registerDTO1 = new RegisterDTO();
        registerDTO1.setUsername("duplicateuser");
        registerDTO1.setFirstname("First");
        registerDTO1.setLastname("User");
        registerDTO1.setEmail("first@test.com");
        registerDTO1.setPassword("password123");

        RegisterDTO registerDTO2 = new RegisterDTO();
        registerDTO2.setUsername("duplicateuser");
        registerDTO2.setFirstname("Second");
        registerDTO2.setLastname("User");
        registerDTO2.setEmail("second@test.com");
        registerDTO2.setPassword("password123");

        // when
        restTemplate.postForEntity("/api/register", registerDTO1, RegisterDTO.class);
        ResponseEntity<Object> response = restTemplate.postForEntity(
                "/api/register",
                registerDTO2,
                Object.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void whenRegisterUserWithDuplicateEmail_thenReturnConflict() {
        // given
        RegisterDTO registerDTO1 = new RegisterDTO();
        registerDTO1.setUsername("user1");
        registerDTO1.setFirstname("First");
        registerDTO1.setLastname("User");
        registerDTO1.setEmail("duplicate@test.com");
        registerDTO1.setPassword("password123");

        RegisterDTO registerDTO2 = new RegisterDTO();
        registerDTO2.setUsername("user2");
        registerDTO2.setFirstname("Second");
        registerDTO2.setLastname("User");
        registerDTO2.setEmail("duplicate@test.com");
        registerDTO2.setPassword("password123");

        // when
        restTemplate.postForEntity("/api/register", registerDTO1, RegisterDTO.class);
        ResponseEntity<Object> response = restTemplate.postForEntity(
                "/api/register",
                registerDTO2,
                Object.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void whenGetNonExistentUser_thenReturnNotFound() {
        // when
        ResponseEntity<Object> response = restTemplate.getForEntity(
                "/api/users/999999",
                Object.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void whenRegisterUserWithInvalidData_thenReturnBadRequest() {
        // given
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername(""); // invalid - empty username
        registerDTO.setFirstname("Test");
        registerDTO.setLastname("User");
        registerDTO.setEmail("invalid-email"); // invalid email
        registerDTO.setPassword("123"); // too short password

        // when
        ResponseEntity<Object> response = restTemplate.postForEntity(
                "/api/register",
                registerDTO,
                Object.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
} 