package org.codevoke.probnb.dtos;

import org.codevoke.probnb.dto.UserDTO;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDTOTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void serialize_ShouldUseSnakeCase() throws Exception {
        // given
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUsername("testuser");
        userDTO.setFirstname("Test");
        userDTO.setLastname("User");
        userDTO.setAvatar(2L);

        // when
        String json = objectMapper.writeValueAsString(userDTO);

        // then
        assertThat(json).contains("\"username\":\"testuser\"");
        assertThat(json).contains("\"firstname\":\"Test\"");
        assertThat(json).contains("\"lastname\":\"User\"");
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
                    "avatar": 2
                }
                """;

        // when
        UserDTO userDTO = objectMapper.readValue(json, UserDTO.class);

        // then
        assertThat(userDTO.getUsername()).isEqualTo("testuser");
        assertThat(userDTO.getFirstname()).isEqualTo("Test");
        assertThat(userDTO.getLastname()).isEqualTo("User");
        assertThat(userDTO.getAvatar()).isEqualTo(2L);
    }

    @Test
    void builder_ShouldCreateUserDTO() {
        // when
        UserDTO userDTO = UserDTO.builder()
                .username("testuser")
                .firstname("Test")
                .lastname("User")
                .avatar(2L)
                .build();

        // then
        assertThat(userDTO.getUsername()).isEqualTo("testuser");
        assertThat(userDTO.getFirstname()).isEqualTo("Test");
        assertThat(userDTO.getLastname()).isEqualTo("User");
        assertThat(userDTO.getAvatar()).isEqualTo(2L);
    }

    @Test
    void equalsAndHashCode_ShouldWorkCorrectly() {
        // given
        UserDTO user1 = UserDTO.builder()
                .username("testuser")
                .firstname("Test")
                .lastname("User")
                .avatar(2L)
                .build();

        UserDTO user2 = UserDTO.builder()
                .username("testuser")
                .firstname("Test")
                .lastname("User")
                .avatar(2L)
                .build();

        UserDTO user3 = UserDTO.builder()
                .username("different")
                .firstname("Test")
                .lastname("User")
                .avatar(2L)
                .build();

        // then
        assertThat(user1).isEqualTo(user2);
        assertThat(user1).isNotEqualTo(user3);
        assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
        assertThat(user1.hashCode()).isNotEqualTo(user3.hashCode());
    }

    @Test
    void toString_ShouldIncludeAllFields() {
        // given
        UserDTO userDTO = UserDTO.builder()
                .username("testuser")
                .firstname("Test")
                .lastname("User")
                .avatar(2L)
                .build();

        // when
        String toString = userDTO.toString();

        // then
        assertThat(toString).contains("testuser");
        assertThat(toString).contains("Test");
        assertThat(toString).contains("User");
        assertThat(toString).contains("2");
    }
} 