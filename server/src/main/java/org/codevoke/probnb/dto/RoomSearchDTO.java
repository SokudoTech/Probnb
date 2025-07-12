package org.codevoke.probnb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "Room Search DTO")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomSearchDTO {
    @Schema(description = "Room ID")
    private Long id;

    @Schema(description = "Room title")
    private String title;

    @Schema(description = "Room short description")
    private String subtitle;

    @Schema(description = "Main image URL")
    @JsonProperty("image_url")
    private String imageUrl;
} 