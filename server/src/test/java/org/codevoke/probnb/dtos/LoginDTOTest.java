package org.codevoke.probnb.dtos;

import org.codevoke.probnb.dto.LoginDTO; 

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class LoginDTOTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void serialize_ShouldUseSnakeCase() throws Exception {
        // given
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("test@example.com");
        loginDTO.setPassword("password123");

        // when
        String json = objectMapper.writeValueAsString(loginDTO);

        // then
        assertThat(json).contains("\"email\":\"test@example.com\"");
        assertThat(json).contains("\"password\":\"password123\"");
    }

    @Test
    void deserialize_ShouldMapSnakeCaseToCamelCase() throws Exception {
        // given
        String json = """
                {
                    "email": "test@example.com",
                    "password": "password123"
                }
                """;

        // when
        LoginDTO loginDTO = objectMapper.readValue(json, LoginDTO.class);

        // then
        assertThat(loginDTO.getEmail()).isEqualTo("test@example.com");
        assertThat(loginDTO.getPassword()).isEqualTo("password123");
    }

    @Test
    void builder_ShouldCreateLoginDTO() {
        // when
        LoginDTO loginDTO = LoginDTO.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        // then
        assertThat(loginDTO.getEmail()).isEqualTo("test@example.com");
        assertThat(loginDTO.getPassword()).isEqualTo("password123");
    }

    @Test
    void equalsAndHashCode_ShouldWorkCorrectly() {
        // given
        LoginDTO login1 = LoginDTO.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        LoginDTO login2 = LoginDTO.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        LoginDTO login3 = LoginDTO.builder()
                .email("different@example.com")
                .password("password123")
                .build();

        // then
        assertThat(login1).isEqualTo(login2);
        assertThat(login1).isNotEqualTo(login3);
        assertThat(login1.hashCode()).isEqualTo(login2.hashCode());
        assertThat(login1.hashCode()).isNotEqualTo(login3.hashCode());
    }

    @Test
    void toString_ShouldIncludeAllFields() {
        // given
        LoginDTO loginDTO = LoginDTO.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        // when
        String toString = loginDTO.toString();

        // then
        assertThat(toString).contains("test@example.com");
        assertThat(toString).contains("password123");
    }

    @Test
    void deserialize_WhenPartialData_ShouldMapCorrectly() throws Exception {
        // given
        String json = """
                {
                    "email": "test@example.com"
                }
                """;

        // when
        LoginDTO loginDTO = objectMapper.readValue(json, LoginDTO.class);

        // then
        assertThat(loginDTO.getEmail()).isEqualTo("test@example.com");
        assertThat(loginDTO.getPassword()).isNull();
    }

    @Test
    void deserialize_WhenEmptyData_ShouldMapCorrectly() throws Exception {
        // given
        String json = "{}";

        // when
        LoginDTO loginDTO = objectMapper.readValue(json, LoginDTO.class);

        // then
        assertThat(loginDTO.getEmail()).isNull();
        assertThat(loginDTO.getPassword()).isNull();
    }

    @Test
    void deserialize_WhenNullValues_ShouldMapCorrectly() throws Exception {
        // given
        String json = """
                {
                    "email": null,
                    "password": null
                }
                """;

        // when
        LoginDTO loginDTO = objectMapper.readValue(json, LoginDTO.class);

        // then
        assertThat(loginDTO.getEmail()).isNull();
        assertThat(loginDTO.getPassword()).isNull();
    }

    @Test
    void deserialize_WhenEmptyStrings_ShouldMapCorrectly() throws Exception {
        // given
        String json = """
                {
                    "email": "",
                    "password": ""
                }
                """;

        // when
        LoginDTO loginDTO = objectMapper.readValue(json, LoginDTO.class);

        // then
        assertThat(loginDTO.getEmail()).isEqualTo("");
        assertThat(loginDTO.getPassword()).isEqualTo("");
    }

    @Test
    void deserialize_WhenSpecialCharacters_ShouldMapCorrectly() throws Exception {
        // given
        String json = """
                {
                    "email": "test+tag@example.com",
                    "password": "p@ssw0rd!@#$%^&*()"
                }
                """;

        // when
        LoginDTO loginDTO = objectMapper.readValue(json, LoginDTO.class);

        // then
        assertThat(loginDTO.getEmail()).isEqualTo("test+tag@example.com");
        assertThat(loginDTO.getPassword()).isEqualTo("p@ssw0rd!@#$%^&*()");
    }

    @Test
    void deserialize_WhenUnicodeCharacters_ShouldMapCorrectly() throws Exception {
        // given
        String json = """
                {
                    "email": "тест@example.com",
                    "password": "пароль123"
                }
                """;

        // when
        LoginDTO loginDTO = objectMapper.readValue(json, LoginDTO.class);

        // then
        assertThat(loginDTO.getEmail()).isEqualTo("тест@example.com");
        assertThat(loginDTO.getPassword()).isEqualTo("пароль123");
    }
} 