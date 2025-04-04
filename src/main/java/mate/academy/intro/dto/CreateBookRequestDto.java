package mate.academy.intro.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class CreateBookRequestDto {
    @NotBlank
    private String title;
    @NotBlank
    private String author;
    @NotNull
    @Size(max = 13, min = 13)
    private String isbn;
    @NotNull
    @Min(value = 0)
    private BigDecimal price;
    @NotBlank
    private String description;
    @NotBlank
    private String coverImage;
    @NotBlank
    private List<Long> categoriesId;
}
