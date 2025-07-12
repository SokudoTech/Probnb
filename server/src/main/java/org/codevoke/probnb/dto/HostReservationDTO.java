package org.codevoke.probnb.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.ZonedDateTime;

@Schema(description = "Register DTO")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HostReservationDTO {
    @Schema(description = "Reservation ID")
    @JsonIgnore
    private Long id;

    @Schema(description = "room ID")
    @JsonProperty("room_id")
    @NotBlank
    private Long roomId;

    @Schema(description = "host ID")
    @JsonProperty("host_id")
    @NotBlank
    private Long hostId;

    @Schema(
            description = "start date",
            pattern = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3}Z",
            example = "2025-03-29T15:18:04.474Z"
    )
    @Future
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    @JsonProperty("date_start")
    private ZonedDateTime dateStart;

    @Future
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    @JsonProperty("date_end")
    private ZonedDateTime dateEnd;
}
