package mate.academy.intro.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateCartItemDto {

    @NotNull
    private Long shoppingCartId;

    @NotNull
    private Long bookId;

    @Min(value = 1)
    private int quantity;
}
