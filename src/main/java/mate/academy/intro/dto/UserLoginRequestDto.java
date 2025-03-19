package mate.academy.intro.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record UserLoginRequestDto(
        @NotEmpty
        @Size(min = 1, max = 35)
        @Email
        String email,

        @NotEmpty
        @Size(min = 1, max = 35)
        String password) {
}
