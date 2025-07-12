package org.codevoke.probnb.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Schema(description = "Register DTO")
@SuperBuilder
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDTO extends UserDTO {
    @Schema(description = "User email")
    @NotBlank(message = "Email is required")
    @Size(min = 5, max = 120)
    @Email
    private String email;

    @Schema(description = "User password")
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 50)
    private String password;
}
