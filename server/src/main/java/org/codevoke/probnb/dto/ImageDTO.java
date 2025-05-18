package org.codevoke.probnb.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "Image DTO")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImageDTO {
    @Schema(description = "image id")
    @JsonIgnore
    private Long id;

    @Schema(description = "image binary")
    private String image;
}
