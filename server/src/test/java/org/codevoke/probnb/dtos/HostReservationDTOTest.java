package org.codevoke.probnb.dtos;

import org.codevoke.probnb.dto.HostReservationDTO;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class HostReservationDTOTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void serialize_ShouldUseSnakeCase() throws Exception {
        // given
        HostReservationDTO hostReservationDTO = new HostReservationDTO();
        hostReservationDTO.setId(1L);
        hostReservationDTO.setRoomId(2L);
        hostReservationDTO.setHostId(3L);
        hostReservationDTO.setDateStart(ZonedDateTime.now().plusHours(1));
        hostReservationDTO.setDateEnd(ZonedDateTime.now().plusHours(2));

        // when
        String json = objectMapper.writeValueAsString(hostReservationDTO);

        // then
        assertThat(json).contains("\"room_id\":2");
        assertThat(json).contains("\"host_id\":3");
        assertThat(json).contains("\"date_start\"");
        assertThat(json).contains("\"date_end\"");
        assertThat(json).doesNotContain("\"id\""); // @JsonIgnore
    }

    @Test
    void deserialize_ShouldMapSnakeCaseToCamelCase() throws Exception {
        // given
        String json = """
                {
                    "room_id": 2,
                    "host_id": 3,
                    "date_start": "2024-01-15T10:00:00Z",
                    "date_end": "2024-01-15T12:00:00Z"
                }
                """;

        // when
        HostReservationDTO hostReservationDTO = objectMapper.readValue(json, HostReservationDTO.class);

        // then
        assertThat(hostReservationDTO.getRoomId()).isEqualTo(2L);
        assertThat(hostReservationDTO.getHostId()).isEqualTo(3L);
        assertThat(hostReservationDTO.getDateStart()).isNotNull();
        assertThat(hostReservationDTO.getDateEnd()).isNotNull();
    }

    @Test
    void builder_ShouldCreateHostReservationDTO() {
        // given
        ZonedDateTime startDate = ZonedDateTime.now().plusHours(1);
        ZonedDateTime endDate = ZonedDateTime.now().plusHours(2);

        // when
        HostReservationDTO hostReservationDTO = HostReservationDTO.builder()
                .roomId(2L)
                .hostId(3L)
                .dateStart(startDate)
                .dateEnd(endDate)
                .build();

        // then
        assertThat(hostReservationDTO.getRoomId()).isEqualTo(2L);
        assertThat(hostReservationDTO.getHostId()).isEqualTo(3L);
        assertThat(hostReservationDTO.getDateStart()).isEqualTo(startDate);
        assertThat(hostReservationDTO.getDateEnd()).isEqualTo(endDate);
    }

    @Test
    void equalsAndHashCode_ShouldWorkCorrectly() {
        // given
        ZonedDateTime startDate = ZonedDateTime.now().plusHours(1);
        ZonedDateTime endDate = ZonedDateTime.now().plusHours(2);

        HostReservationDTO hostReservation1 = HostReservationDTO.builder()
                .roomId(2L)
                .hostId(3L)
                .dateStart(startDate)
                .dateEnd(endDate)
                .build();

        HostReservationDTO hostReservation2 = HostReservationDTO.builder()
                .roomId(2L)
                .hostId(3L)
                .dateStart(startDate)
                .dateEnd(endDate)
                .build();

        HostReservationDTO hostReservation3 = HostReservationDTO.builder()
                .roomId(5L)
                .hostId(3L)
                .dateStart(startDate)
                .dateEnd(endDate)
                .build();

        // then
        assertThat(hostReservation1).isEqualTo(hostReservation2);
        assertThat(hostReservation1).isNotEqualTo(hostReservation3);
        assertThat(hostReservation1.hashCode()).isEqualTo(hostReservation2.hashCode());
        assertThat(hostReservation1.hashCode()).isNotEqualTo(hostReservation3.hashCode());
    }

    @Test
    void toString_ShouldIncludeAllFields() {
        // given
        ZonedDateTime startDate = ZonedDateTime.now().plusHours(1);
        ZonedDateTime endDate = ZonedDateTime.now().plusHours(2);

        HostReservationDTO hostReservationDTO = HostReservationDTO.builder()
                .roomId(2L)
                .hostId(3L)
                .dateStart(startDate)
                .dateEnd(endDate)
                .build();

        // when
        String toString = hostReservationDTO.toString();

        // then
        assertThat(toString).contains("2");
        assertThat(toString).contains("3");
        assertThat(toString).contains(startDate.toString());
        assertThat(toString).contains(endDate.toString());
    }

    @Test
    void deserialize_WhenPartialData_ShouldMapCorrectly() throws Exception {
        // given
        String json = """
                {
                    "room_id": 2,
                    "host_id": 3
                }
                """;

        // when
        HostReservationDTO hostReservationDTO = objectMapper.readValue(json, HostReservationDTO.class);

        // then
        assertThat(hostReservationDTO.getRoomId()).isEqualTo(2L);
        assertThat(hostReservationDTO.getHostId()).isEqualTo(3L);
        assertThat(hostReservationDTO.getDateStart()).isNull();
        assertThat(hostReservationDTO.getDateEnd()).isNull();
    }

    @Test
    void deserialize_WhenEmptyData_ShouldMapCorrectly() throws Exception {
        // given
        String json = "{}";

        // when
        HostReservationDTO hostReservationDTO = objectMapper.readValue(json, HostReservationDTO.class);

        // then
        assertThat(hostReservationDTO.getRoomId()).isNull();
        assertThat(hostReservationDTO.getHostId()).isNull();
        assertThat(hostReservationDTO.getDateStart()).isNull();
        assertThat(hostReservationDTO.getDateEnd()).isNull();
    }

    @Test
    void deserialize_WhenDifferentTimeZone_ShouldMapCorrectly() throws Exception {
        // given
        String json = """
                {
                    "room_id": 2,
                    "host_id": 3,
                    "date_start": "2024-01-15T10:00:00+03:00",
                    "date_end": "2024-01-15T12:00:00+03:00"
                }
                """;

        // when
        HostReservationDTO hostReservationDTO = objectMapper.readValue(json, HostReservationDTO.class);

        // then
        assertThat(hostReservationDTO.getRoomId()).isEqualTo(2L);
        assertThat(hostReservationDTO.getHostId()).isEqualTo(3L);
        assertThat(hostReservationDTO.getDateStart()).isNotNull();
        assertThat(hostReservationDTO.getDateEnd()).isNotNull();
    }
} 