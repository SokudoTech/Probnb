package org.codevoke.probnb.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.ZonedDateTime;

@Schema(description = "Room Filter DTO")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomFilterDTO {
    @Schema(description = "Room type filter (optional)")
    @JsonProperty("room_type")
    private String roomType;

    @Schema(description = "Number of rooms filter (optional)")
    @JsonProperty("rooms_count")
    private Integer roomsCount;

    @Schema(description = "Location filter (optional)")
    private String location;

    @Schema(
            description = "Check-in date (optional)",
            pattern = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3}Z",
            example = "2025-03-29T15:18:04.474Z"
    )
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    @JsonProperty("check_in_date")
    private ZonedDateTime checkInDate;

    @Schema(
            description = "Check-out date (optional)",
            pattern = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3}Z",
            example = "2025-04-05T15:18:04.474Z"
    )
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    @JsonProperty("check_out_date")
    private ZonedDateTime checkOutDate;
} 