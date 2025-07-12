package org.codevoke.probnb.dtos;

import org.codevoke.probnb.dto.RoomImageDTO;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class RoomImageDTOTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void serialize_ShouldUseSnakeCase() throws Exception {
        // given
        RoomImageDTO roomImageDTO = new RoomImageDTO();
        roomImageDTO.setId(1L);
        roomImageDTO.setRoomId(2L);
        roomImageDTO.setImageId(3L);

        // when
        String json = objectMapper.writeValueAsString(roomImageDTO);

        // then
        assertThat(json).contains("\"room_id\":2");
        assertThat(json).contains("\"image_id\":3");
        assertThat(json).doesNotContain("\"id\""); // @JsonIgnore
    }

    @Test
    void deserialize_ShouldMapSnakeCaseToCamelCase() throws Exception {
        // given
        String json = """
                {
                    "room_id": 2,
                    "image_id": 3
                }
                """;

        // when
        RoomImageDTO roomImageDTO = objectMapper.readValue(json, RoomImageDTO.class);

        // then
        assertThat(roomImageDTO.getRoomId()).isEqualTo(2L);
        assertThat(roomImageDTO.getImageId()).isEqualTo(3L);
    }

    @Test
    void builder_ShouldCreateRoomImageDTO() {
        // when
        RoomImageDTO roomImageDTO = RoomImageDTO.builder()
                .roomId(2L)
                .imageId(3L)
                .build();

        // then
        assertThat(roomImageDTO.getRoomId()).isEqualTo(2L);
        assertThat(roomImageDTO.getImageId()).isEqualTo(3L);
    }

    @Test
    void equalsAndHashCode_ShouldWorkCorrectly() {
        // given
        RoomImageDTO roomImage1 = RoomImageDTO.builder()
                .roomId(2L)
                .imageId(3L)
                .build();

        RoomImageDTO roomImage2 = RoomImageDTO.builder()
                .roomId(2L)
                .imageId(3L)
                .build();

        RoomImageDTO roomImage3 = RoomImageDTO.builder()
                .roomId(5L)
                .imageId(3L)
                .build();

        // then
        assertThat(roomImage1).isEqualTo(roomImage2);
        assertThat(roomImage1).isNotEqualTo(roomImage3);
        assertThat(roomImage1.hashCode()).isEqualTo(roomImage2.hashCode());
        assertThat(roomImage1.hashCode()).isNotEqualTo(roomImage3.hashCode());
    }

    @Test
    void toString_ShouldIncludeAllFields() {
        // given
        RoomImageDTO roomImageDTO = RoomImageDTO.builder()
                .roomId(2L)
                .imageId(3L)
                .build();

        // when
        String toString = roomImageDTO.toString();

        // then
        assertThat(toString).contains("2");
        assertThat(toString).contains("3");
    }

    @Test
    void deserialize_WhenPartialData_ShouldMapCorrectly() throws Exception {
        // given
        String json = """
                {
                    "room_id": 2
                }
                """;

        // when
        RoomImageDTO roomImageDTO = objectMapper.readValue(json, RoomImageDTO.class);

        // then
        assertThat(roomImageDTO.getRoomId()).isEqualTo(2L);
        assertThat(roomImageDTO.getImageId()).isNull();
    }

    @Test
    void deserialize_WhenEmptyData_ShouldMapCorrectly() throws Exception {
        // given
        String json = "{}";

        // when
        RoomImageDTO roomImageDTO = objectMapper.readValue(json, RoomImageDTO.class);

        // then
        assertThat(roomImageDTO.getRoomId()).isNull();
        assertThat(roomImageDTO.getImageId()).isNull();
    }
} 