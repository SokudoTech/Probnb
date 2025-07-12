package org.codevoke.probnb.dtos;

import org.codevoke.probnb.dto.RegisterDTO;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class RegisterDTOTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void serialize_ShouldUseSnakeCase() throws Exception {
        // given
        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setUsername("testuser");
        registerDTO.setFirstname("Test");
        registerDTO.setLastname("User");
        registerDTO.setEmail("test@example.com");
        registerDTO.setPassword("password123");
        registerDTO.setAvatar(2L);

        // when
        String json = objectMapper.writeValueAsString(registerDTO);

        // then
        assertThat(json).contains("\"username\":\"testuser\"");
        assertThat(json).contains("\"firstname\":\"Test\"");
        assertThat(json).contains("\"lastname\":\"User\"");
        assertThat(json).contains("\"email\":\"test@example.com\"");
        assertThat(json).contains("\"password\":\"password123\"");
        assertThat(json).contains("\"avatar\":2");
        assertThat(json).doesNotContain("\"id\""); // @JsonIgnore
    }

    @Test
    void deserialize_ShouldMapSnakeCaseToCamelCase() throws Exception {
        // given
        String json = """
                {
                    "username": "testuser",
                    "firstname": "Test",
                    "lastname": "User",
                    "email": "test@example.com",
                    "password": "password123",
                    "avatar": 2
                }
                """;

        // when
        RegisterDTO registerDTO = objectMapper.readValue(json, RegisterDTO.class);

        // then
        assertThat(registerDTO.getUsername()).isEqualTo("testuser");
        assertThat(registerDTO.getFirstname()).isEqualTo("Test");
        assertThat(registerDTO.getLastname()).isEqualTo("User");
        assertThat(registerDTO.getEmail()).isEqualTo("test@example.com");
        assertThat(registerDTO.getPassword()).isEqualTo("password123");
        assertThat(registerDTO.getAvatar()).isEqualTo(2L);
    }

    @Test
    void builder_ShouldCreateRegisterDTO() {
        // when
        RegisterDTO registerDTO = RegisterDTO.builder()
                .username("testuser")
                .firstname("Test")
                .lastname("User")
                .email("test@example.com")
                .password("password123")
                .avatar(2L)
                .build();

        // then
        assertThat(registerDTO.getUsername()).isEqualTo("testuser");
        assertThat(registerDTO.getFirstname()).isEqualTo("Test");
        assertThat(registerDTO.getLastname()).isEqualTo("User");
        assertThat(registerDTO.getEmail()).isEqualTo("test@example.com");
        assertThat(registerDTO.getPassword()).isEqualTo("password123");
        assertThat(registerDTO.getAvatar()).isEqualTo(2L);
    }

    @Test
    void equalsAndHashCode_ShouldWorkCorrectly() {
        // given
        RegisterDTO register1 = RegisterDTO.builder()
                .username("testuser")
                .firstname("Test")
                .lastname("User")
                .email("test@example.com")
                .password("password123")
                .avatar(2L)
                .build();

        RegisterDTO register2 = RegisterDTO.builder()
                .username("testuser")
                .firstname("Test")
                .lastname("User")
                .email("test@example.com")
                .password("password123")
                .avatar(2L)
                .build();

        RegisterDTO register3 = RegisterDTO.builder()
                .username("differentuser")
                .firstname("Test")
                .lastname("User")
                .email("test@example.com")
                .password("password123")
                .avatar(2L)
                .build();

        // then
        assertThat(register1).isEqualTo(register2);
        assertThat(register1).isNotEqualTo(register3);
        assertThat(register1.hashCode()).isEqualTo(register2.hashCode());
        assertThat(register1.hashCode()).isNotEqualTo(register3.hashCode());
    }

    @Test
    void toString_ShouldIncludeAllFields() {
        // given
        RegisterDTO registerDTO = RegisterDTO.builder()
                .username("testuser")
                .firstname("Test")
                .lastname("User")
                .email("test@example.com")
                .password("password123")
                .avatar(2L)
                .build();

        // when
        String toString = registerDTO.toString();

        // then
        assertThat(toString).contains("testuser");
        assertThat(toString).contains("Test");
        assertThat(toString).contains("User");
        assertThat(toString).contains("test@example.com");
        assertThat(toString).contains("password123");
        assertThat(toString).contains("2");
    }

    @Test
    void deserialize_WhenPartialData_ShouldMapCorrectly() throws Exception {
        // given
        String json = """
                {
                    "username": "testuser",
                    "firstname": "Test"
                }
                """;

        // when
        RegisterDTO registerDTO = objectMapper.readValue(json, RegisterDTO.class);

        // then
        assertThat(registerDTO.getUsername()).isEqualTo("testuser");
        assertThat(registerDTO.getFirstname()).isEqualTo("Test");
        assertThat(registerDTO.getLastname()).isNull();
        assertThat(registerDTO.getEmail()).isNull();
        assertThat(registerDTO.getPassword()).isNull();
        assertThat(registerDTO.getAvatar()).isNull();
    }

    @Test
    void deserialize_WhenEmptyData_ShouldMapCorrectly() throws Exception {
        // given
        String json = "{}";

        // when
        RegisterDTO registerDTO = objectMapper.readValue(json, RegisterDTO.class);

        // then
        assertThat(registerDTO.getUsername()).isNull();
        assertThat(registerDTO.getFirstname()).isNull();
        assertThat(registerDTO.getLastname()).isNull();
        assertThat(registerDTO.getEmail()).isNull();
        assertThat(registerDTO.getPassword()).isNull();
        assertThat(registerDTO.getAvatar()).isNull();
    }

    @Test
    void deserialize_WhenNullValues_ShouldMapCorrectly() throws Exception {
        // given
        String json = """
                {
                    "username": null,
                    "firstname": null,
                    "lastname": null,
                    "email": null,
                    "password": null,
                    "avatar": null
                }
                """;

        // when
        RegisterDTO registerDTO = objectMapper.readValue(json, RegisterDTO.class);

        // then
        assertThat(registerDTO.getUsername()).isNull();
        assertThat(registerDTO.getFirstname()).isNull();
        assertThat(registerDTO.getLastname()).isNull();
        assertThat(registerDTO.getEmail()).isNull();
        assertThat(registerDTO.getPassword()).isNull();
        assertThat(registerDTO.getAvatar()).isNull();
    }

    @Test
    void deserialize_WhenEmptyStrings_ShouldMapCorrectly() throws Exception {
        // given
        String json = """
                {
                    "username": "",
                    "firstname": "",
                    "lastname": "",
                    "email": "",
                    "password": ""
                }
                """;

        // when
        RegisterDTO registerDTO = objectMapper.readValue(json, RegisterDTO.class);

        // then
        assertThat(registerDTO.getUsername()).isEqualTo("");
        assertThat(registerDTO.getFirstname()).isEqualTo("");
        assertThat(registerDTO.getLastname()).isEqualTo("");
        assertThat(registerDTO.getEmail()).isEqualTo("");
        assertThat(registerDTO.getPassword()).isEqualTo("");
    }

    @Test
    void deserialize_WhenSpecialCharacters_ShouldMapCorrectly() throws Exception {
        // given
        String json = """
                {
                    "username": "test_user-123",
                    "firstname": "Test",
                    "lastname": "User",
                    "email": "test+tag@example.com",
                    "password": "p@ssw0rd!@#$%^&*()"
                }
                """;

        // when
        RegisterDTO registerDTO = objectMapper.readValue(json, RegisterDTO.class);

        // then
        assertThat(registerDTO.getUsername()).isEqualTo("test_user-123");
        assertThat(registerDTO.getFirstname()).isEqualTo("Test");
        assertThat(registerDTO.getLastname()).isEqualTo("User");
        assertThat(registerDTO.getEmail()).isEqualTo("test+tag@example.com");
        assertThat(registerDTO.getPassword()).isEqualTo("p@ssw0rd!@#$%^&*()");
    }

    @Test
    void deserialize_WhenUnicodeCharacters_ShouldMapCorrectly() throws Exception {
        // given
        String json = """
                {
                    "username": "тест_пользователь",
                    "firstname": "Тест",
                    "lastname": "Пользователь",
                    "email": "тест@example.com",
                    "password": "пароль123"
                }
                """;

        // when
        RegisterDTO registerDTO = objectMapper.readValue(json, RegisterDTO.class);

        // then
        assertThat(registerDTO.getUsername()).isEqualTo("тест_пользователь");
        assertThat(registerDTO.getFirstname()).isEqualTo("Тест");
        assertThat(registerDTO.getLastname()).isEqualTo("Пользователь");
        assertThat(registerDTO.getEmail()).isEqualTo("тест@example.com");
        assertThat(registerDTO.getPassword()).isEqualTo("пароль123");
    }
} 