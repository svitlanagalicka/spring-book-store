package mate.academy.intro.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartItemRequestDto {

    @NotNull
    private Long bookId;

    @Min(value = 200)
    private int quantity;
}
