package mate.academy.intro.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NonNull;

@Data
public class CreateBookRequestDto {
    @NonNull
    private String title;
    @NonNull
    private String author;
    @NonNull
    private String isbn;
    @NonNull
    @Min(value = 0)
    private Double price;
    @NonNull
    private String description;
    @NonNull
    private String coverImage;
}
