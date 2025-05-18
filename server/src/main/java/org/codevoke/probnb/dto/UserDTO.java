package org.codevoke.probnb.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Schema(description = "User DTO")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    @Schema(description = "User ID")
    @JsonIgnore
    private Long id;

    @Schema(description = "User username")
    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    @Schema(description = "User firstname")
    @NotBlank
    @Size(max = 50)
    private String firstname;

    @Schema(description = "User lastname")
    @Size(max = 50)
    private String lastname;

    @Schema(description = "ID of the avatar image")
    private Long avatar;
}
