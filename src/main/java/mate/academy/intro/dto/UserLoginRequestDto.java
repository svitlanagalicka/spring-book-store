package mate.academy.intro.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserLoginRequestDto(
        @NotBlank
        @Size(min = 1, max = 35)
        @Email
        String email,

        @NotBlank
        @Size(min = 1, max = 35)
        String password) {
}
