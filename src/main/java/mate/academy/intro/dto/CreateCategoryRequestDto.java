package mate.academy.intro.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateCategoryRequestDto {
    @NotBlank
    private String name;
    private String description;
}
