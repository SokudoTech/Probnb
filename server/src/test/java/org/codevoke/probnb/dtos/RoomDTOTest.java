package org.codevoke.probnb.dtos;

import org.codevoke.probnb.dto.RoomDTO;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class RoomDTOTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void serialize_ShouldUseSnakeCase() throws Exception {
        // given
        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setId(1L);
        roomDTO.setTitle("Test Room");
        roomDTO.setSubtitle("A test room");
        roomDTO.setDescription("This is a test room");
        roomDTO.setPrice(100L);
        roomDTO.setRoomsCount(2);
        roomDTO.setLocation("Test City");
        roomDTO.setRoomType("Apartment");

        // when
        String json = objectMapper.writeValueAsString(roomDTO);

        // then
        assertThat(json).contains("\"title\":\"Test Room\"");
        assertThat(json).contains("\"subtitle\":\"A test room\"");
        assertThat(json).contains("\"description\":\"This is a test room\"");
        assertThat(json).contains("\"price\":100");
        assertThat(json).contains("\"rooms_count\":2");
        assertThat(json).contains("\"location\":\"Test City\"");
        assertThat(json).contains("\"room_type\":\"Apartment\"");
        assertThat(json).doesNotContain("\"id\""); // @JsonIgnore
    }

    @Test
    void deserialize_ShouldMapSnakeCaseToCamelCase() throws Exception {
        // given
        String json = """
                {
                    "title": "Test Room",
                    "subtitle": "A test room",
                    "description": "This is a test room",
                    "price": 100,
                    "rooms_count": 2,
                    "location": "Test City",
                    "room_type": "Apartment"
                }
                """;

        // when
        RoomDTO roomDTO = objectMapper.readValue(json, RoomDTO.class);

        // then
        assertThat(roomDTO.getTitle()).isEqualTo("Test Room");
        assertThat(roomDTO.getSubtitle()).isEqualTo("A test room");
        assertThat(roomDTO.getDescription()).isEqualTo("This is a test room");
        assertThat(roomDTO.getPrice()).isEqualTo(100L);
        assertThat(roomDTO.getRoomsCount()).isEqualTo(2);
        assertThat(roomDTO.getLocation()).isEqualTo("Test City");
        assertThat(roomDTO.getRoomType()).isEqualTo("Apartment");
    }

    @Test
    void builder_ShouldCreateRoomDTO() {
        // when
        RoomDTO roomDTO = RoomDTO.builder()
                .title("Test Room")
                .subtitle("A test room")
                .description("This is a test room")
                .price(100L)
                .roomsCount(2)
                .location("Test City")
                .roomType("Apartment")
                .build();

        // then
        assertThat(roomDTO.getTitle()).isEqualTo("Test Room");
        assertThat(roomDTO.getSubtitle()).isEqualTo("A test room");
        assertThat(roomDTO.getDescription()).isEqualTo("This is a test room");
        assertThat(roomDTO.getPrice()).isEqualTo(100L);
        assertThat(roomDTO.getRoomsCount()).isEqualTo(2);
        assertThat(roomDTO.getLocation()).isEqualTo("Test City");
        assertThat(roomDTO.getRoomType()).isEqualTo("Apartment");
    }

    @Test
    void equalsAndHashCode_ShouldWorkCorrectly() {
        // given
        RoomDTO room1 = RoomDTO.builder()
                .title("Test Room")
                .subtitle("A test room")
                .description("This is a test room")
                .price(100L)
                .roomsCount(2)
                .location("Test City")
                .roomType("Apartment")
                .build();

        RoomDTO room2 = RoomDTO.builder()
                .title("Test Room")
                .subtitle("A test room")
                .description("This is a test room")
                .price(100L)
                .roomsCount(2)
                .location("Test City")
                .roomType("Apartment")
                .build();

        RoomDTO room3 = RoomDTO.builder()
                .title("Different Room")
                .subtitle("A test room")
                .description("This is a test room")
                .price(100L)
                .roomsCount(2)
                .location("Test City")
                .roomType("Apartment")
                .build();

        // then
        assertThat(room1).isEqualTo(room2);
        assertThat(room1).isNotEqualTo(room3);
        assertThat(room1.hashCode()).isEqualTo(room2.hashCode());
        assertThat(room1.hashCode()).isNotEqualTo(room3.hashCode());
    }

    @Test
    void toString_ShouldIncludeAllFields() {
        // given
        RoomDTO roomDTO = RoomDTO.builder()
                .title("Test Room")
                .subtitle("A test room")
                .description("This is a test room")
                .price(100L)
                .roomsCount(2)
                .location("Test City")
                .roomType("Apartment")
                .build();

        // when
        String toString = roomDTO.toString();

        // then
        assertThat(toString).contains("Test Room");
        assertThat(toString).contains("A test room");
        assertThat(toString).contains("This is a test room");
        assertThat(toString).contains("100");
        assertThat(toString).contains("2");
        assertThat(toString).contains("Test City");
        assertThat(toString).contains("Apartment");
    }
} 