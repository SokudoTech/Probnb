package org.codevoke.probnb.dtos;

import org.codevoke.probnb.dto.RoomFilterDTO;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class RoomFilterDTOTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void serialize_ShouldUseSnakeCase() throws Exception {
        // given
        RoomFilterDTO filterDTO = new RoomFilterDTO();
        filterDTO.setRoomType("Apartment");
        filterDTO.setRoomsCount(2);
        filterDTO.setLocation("Moscow");
        filterDTO.setCheckInDate(ZonedDateTime.now().plusDays(1));
        filterDTO.setCheckOutDate(ZonedDateTime.now().plusDays(3));

        // when
        String json = objectMapper.writeValueAsString(filterDTO);

        // then
        assertThat(json).contains("\"room_type\":\"Apartment\"");
        assertThat(json).contains("\"rooms_count\":2");
        assertThat(json).contains("\"location\":\"Moscow\"");
        assertThat(json).contains("\"check_in_date\"");
        assertThat(json).contains("\"check_out_date\"");
    }

    @Test
    void deserialize_ShouldMapSnakeCaseToCamelCase() throws Exception {
        // given
        String json = """
                {
                    "room_type": "Apartment",
                    "rooms_count": 2,
                    "location": "Moscow",
                    "check_in_date": "2024-01-15T10:00:00Z",
                    "check_out_date": "2024-01-17T10:00:00Z"
                }
                """;

        // when
        RoomFilterDTO filterDTO = objectMapper.readValue(json, RoomFilterDTO.class);

        // then
        assertThat(filterDTO.getRoomType()).isEqualTo("Apartment");
        assertThat(filterDTO.getRoomsCount()).isEqualTo(2);
        assertThat(filterDTO.getLocation()).isEqualTo("Moscow");
        assertThat(filterDTO.getCheckInDate()).isNotNull();
        assertThat(filterDTO.getCheckOutDate()).isNotNull();
    }

    @Test
    void builder_ShouldCreateRoomFilterDTO() {
        // given
        ZonedDateTime checkIn = ZonedDateTime.now().plusDays(1);
        ZonedDateTime checkOut = ZonedDateTime.now().plusDays(3);

        // when
        RoomFilterDTO filterDTO = RoomFilterDTO.builder()
                .roomType("Apartment")
                .roomsCount(2)
                .location("Moscow")
                .checkInDate(checkIn)
                .checkOutDate(checkOut)
                .build();

        // then
        assertThat(filterDTO.getRoomType()).isEqualTo("Apartment");
        assertThat(filterDTO.getRoomsCount()).isEqualTo(2);
        assertThat(filterDTO.getLocation()).isEqualTo("Moscow");
        assertThat(filterDTO.getCheckInDate()).isEqualTo(checkIn);
        assertThat(filterDTO.getCheckOutDate()).isEqualTo(checkOut);
    }

    @Test
    void equalsAndHashCode_ShouldWorkCorrectly() {
        // given
        ZonedDateTime checkIn = ZonedDateTime.now().plusDays(1);
        ZonedDateTime checkOut = ZonedDateTime.now().plusDays(3);

        RoomFilterDTO filter1 = RoomFilterDTO.builder()
                .roomType("Apartment")
                .roomsCount(2)
                .location("Moscow")
                .checkInDate(checkIn)
                .checkOutDate(checkOut)
                .build();

        RoomFilterDTO filter2 = RoomFilterDTO.builder()
                .roomType("Apartment")
                .roomsCount(2)
                .location("Moscow")
                .checkInDate(checkIn)
                .checkOutDate(checkOut)
                .build();

        RoomFilterDTO filter3 = RoomFilterDTO.builder()
                .roomType("House")
                .roomsCount(2)
                .location("Moscow")
                .checkInDate(checkIn)
                .checkOutDate(checkOut)
                .build();

        // then
        assertThat(filter1).isEqualTo(filter2);
        assertThat(filter1).isNotEqualTo(filter3);
        assertThat(filter1.hashCode()).isEqualTo(filter2.hashCode());
        assertThat(filter1.hashCode()).isNotEqualTo(filter3.hashCode());
    }

    @Test
    void toString_ShouldIncludeAllFields() {
        // given
        ZonedDateTime checkIn = ZonedDateTime.now().plusDays(1);
        ZonedDateTime checkOut = ZonedDateTime.now().plusDays(3);

        RoomFilterDTO filterDTO = RoomFilterDTO.builder()
                .roomType("Apartment")
                .roomsCount(2)
                .location("Moscow")
                .checkInDate(checkIn)
                .checkOutDate(checkOut)
                .build();

        // when
        String toString = filterDTO.toString();

        // then
        assertThat(toString).contains("Apartment");
        assertThat(toString).contains("2");
        assertThat(toString).contains("Moscow");
        assertThat(toString).contains(checkIn.toString());
        assertThat(toString).contains(checkOut.toString());
    }

    @Test
    void deserialize_WhenPartialData_ShouldMapCorrectly() throws Exception {
        // given
        String json = """
                {
                    "room_type": "Apartment",
                    "rooms_count": 2
                }
                """;

        // when
        RoomFilterDTO filterDTO = objectMapper.readValue(json, RoomFilterDTO.class);

        // then
        assertThat(filterDTO.getRoomType()).isEqualTo("Apartment");
        assertThat(filterDTO.getRoomsCount()).isEqualTo(2);
        assertThat(filterDTO.getLocation()).isNull();
        assertThat(filterDTO.getCheckInDate()).isNull();
        assertThat(filterDTO.getCheckOutDate()).isNull();
    }

    @Test
    void deserialize_WhenEmptyData_ShouldMapCorrectly() throws Exception {
        // given
        String json = "{}";

        // when
        RoomFilterDTO filterDTO = objectMapper.readValue(json, RoomFilterDTO.class);

        // then
        assertThat(filterDTO.getRoomType()).isNull();
        assertThat(filterDTO.getRoomsCount()).isNull();
        assertThat(filterDTO.getLocation()).isNull();
        assertThat(filterDTO.getCheckInDate()).isNull();
        assertThat(filterDTO.getCheckOutDate()).isNull();
    }
} 