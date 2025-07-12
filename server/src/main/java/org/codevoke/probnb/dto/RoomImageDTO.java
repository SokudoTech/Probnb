package org.codevoke.probnb.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Schema(description = "RoomImage DTO")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomImageDTO {
    @Schema(description = "Reservation ID")
    @JsonIgnore
    private Long id;

    @Schema(description = "title of image")
    private String title;

    @Schema(description = "description of room image")
    private String description;

    @Schema(description = "room ID")
    @JsonProperty("room_id")
    @NotNull
    private Long roomId;

    @Schema(description = "image ID")
    @JsonProperty("image_id")
    @NotNull
    private Long imageId;
}
