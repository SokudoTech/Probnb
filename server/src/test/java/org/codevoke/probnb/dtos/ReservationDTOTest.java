package org.codevoke.probnb.dtos;

import org.codevoke.probnb.dto.ReservationDTO;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ReservationDTOTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void serialize_ShouldUseSnakeCase() throws Exception {
        // given
        ReservationDTO reservationDTO = new ReservationDTO();
        reservationDTO.setId(1L);
        reservationDTO.setRoomId(2L);
        reservationDTO.setUserId(3L);
        reservationDTO.setHostId(4L);
        reservationDTO.setDateStart(ZonedDateTime.now().plusHours(1));
        reservationDTO.setDateEnd(ZonedDateTime.now().plusHours(2));

        // when
        String json = objectMapper.writeValueAsString(reservationDTO);

        // then
        assertThat(json).contains("\"room_id\":2");
        assertThat(json).contains("\"user_id\":3");
        assertThat(json).contains("\"host_id\":4");
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
                    "user_id": 3,
                    "host_id": 4,
                    "date_start": "2024-01-15T10:00:00Z",
                    "date_end": "2024-01-15T12:00:00Z"
                }
                """;

        // when
        ReservationDTO reservationDTO = objectMapper.readValue(json, ReservationDTO.class);

        // then
        assertThat(reservationDTO.getRoomId()).isEqualTo(2L);
        assertThat(reservationDTO.getUserId()).isEqualTo(3L);
        assertThat(reservationDTO.getHostId()).isEqualTo(4L);
        assertThat(reservationDTO.getDateStart()).isNotNull();
        assertThat(reservationDTO.getDateEnd()).isNotNull();
    }

    @Test
    void builder_ShouldCreateReservationDTO() {
        // given
        ZonedDateTime startDate = ZonedDateTime.now().plusHours(1);
        ZonedDateTime endDate = ZonedDateTime.now().plusHours(2);

        // when
        ReservationDTO reservationDTO = ReservationDTO.builder()
                .roomId(2L)
                .userId(3L)
                .hostId(4L)
                .dateStart(startDate)
                .dateEnd(endDate)
                .build();

        // then
        assertThat(reservationDTO.getRoomId()).isEqualTo(2L);
        assertThat(reservationDTO.getUserId()).isEqualTo(3L);
        assertThat(reservationDTO.getHostId()).isEqualTo(4L);
        assertThat(reservationDTO.getDateStart()).isEqualTo(startDate);
        assertThat(reservationDTO.getDateEnd()).isEqualTo(endDate);
    }

    @Test
    void equalsAndHashCode_ShouldWorkCorrectly() {
        // given
        ZonedDateTime startDate = ZonedDateTime.now().plusHours(1);
        ZonedDateTime endDate = ZonedDateTime.now().plusHours(2);

        ReservationDTO reservation1 = ReservationDTO.builder()
                .roomId(2L)
                .userId(3L)
                .hostId(4L)
                .dateStart(startDate)
                .dateEnd(endDate)
                .build();

        ReservationDTO reservation2 = ReservationDTO.builder()
                .roomId(2L)
                .userId(3L)
                .hostId(4L)
                .dateStart(startDate)
                .dateEnd(endDate)
                .build();

        ReservationDTO reservation3 = ReservationDTO.builder()
                .roomId(5L)
                .userId(3L)
                .hostId(4L)
                .dateStart(startDate)
                .dateEnd(endDate)
                .build();

        // then
        assertThat(reservation1).isEqualTo(reservation2);
        assertThat(reservation1).isNotEqualTo(reservation3);
        assertThat(reservation1.hashCode()).isEqualTo(reservation2.hashCode());
        assertThat(reservation1.hashCode()).isNotEqualTo(reservation3.hashCode());
    }

    @Test
    void toString_ShouldIncludeAllFields() {
        // given
        ZonedDateTime startDate = ZonedDateTime.now().plusHours(1);
        ZonedDateTime endDate = ZonedDateTime.now().plusHours(2);

        ReservationDTO reservationDTO = ReservationDTO.builder()
                .roomId(2L)
                .userId(3L)
                .hostId(4L)
                .dateStart(startDate)
                .dateEnd(endDate)
                .build();

        // when
        String toString = reservationDTO.toString();

        // then
        assertThat(toString).contains("2");
        assertThat(toString).contains("3");
        assertThat(toString).contains("4");
        assertThat(toString).contains(startDate.toString());
        assertThat(toString).contains(endDate.toString());
    }

    @Test
    void deserialize_WhenPartialData_ShouldMapCorrectly() throws Exception {
        // given
        String json = """
                {
                    "room_id": 2,
                    "user_id": 3
                }
                """;

        // when
        ReservationDTO reservationDTO = objectMapper.readValue(json, ReservationDTO.class);

        // then
        assertThat(reservationDTO.getRoomId()).isEqualTo(2L);
        assertThat(reservationDTO.getUserId()).isEqualTo(3L);
        assertThat(reservationDTO.getHostId()).isNull();
        assertThat(reservationDTO.getDateStart()).isNull();
        assertThat(reservationDTO.getDateEnd()).isNull();
    }

    @Test
    void deserialize_WhenEmptyData_ShouldMapCorrectly() throws Exception {
        // given
        String json = "{}";

        // when
        ReservationDTO reservationDTO = objectMapper.readValue(json, ReservationDTO.class);

        // then
        assertThat(reservationDTO.getRoomId()).isNull();
        assertThat(reservationDTO.getUserId()).isNull();
        assertThat(reservationDTO.getHostId()).isNull();
        assertThat(reservationDTO.getDateStart()).isNull();
        assertThat(reservationDTO.getDateEnd()).isNull();
    }

    @Test
    void deserialize_WhenDifferentTimeZone_ShouldMapCorrectly() throws Exception {
        // given
        String json = """
                {
                    "room_id": 2,
                    "user_id": 3,
                    "host_id": 4,
                    "date_start": "2024-01-15T10:00:00+03:00",
                    "date_end": "2024-01-15T12:00:00+03:00"
                }
                """;

        // when
        ReservationDTO reservationDTO = objectMapper.readValue(json, ReservationDTO.class);

        // then
        assertThat(reservationDTO.getRoomId()).isEqualTo(2L);
        assertThat(reservationDTO.getUserId()).isEqualTo(3L);
        assertThat(reservationDTO.getHostId()).isEqualTo(4L);
        assertThat(reservationDTO.getDateStart()).isNotNull();
        assertThat(reservationDTO.getDateEnd()).isNotNull();
    }
} 