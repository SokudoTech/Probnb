package org.codevoke.probnb.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jdk.jfr.Unsigned;
import lombok.*;

@Schema(description = "Register DTO")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomDTO {
    @Schema(description = "Room ID")
    @JsonIgnore
    private Long id;

    @Schema(description = "Room title")
    @Size(min = 5, max = 50)
    @NotBlank(groups = OnCreate.class)
    private String title;

    @Schema(description = "Room short description")
    @Size(max = 200)
    @NotBlank(groups = OnCreate.class)
    private String subtitle;

    @Schema(description = "Room description")
    @Size(max = 1000)
    @NotBlank(groups = OnCreate.class)
    private String description;

    @Schema(description = "price per day")
    @NotBlank(groups = OnCreate.class)
    private Long price;

    @Schema(description = "price per day")
    @JsonProperty("rooms_count")
    @NotBlank(groups = OnCreate.class)
    private Integer roomsCount;

    @Schema(description = "Room location")
    @Size(max = 100)
    @NotBlank(groups = OnCreate.class)
    private String location;

    @Schema(description = "Room Type")
    @Size(max = 100)
    @JsonProperty("room_type")
    @NotBlank(groups = OnCreate.class)
    private String roomType;
}
