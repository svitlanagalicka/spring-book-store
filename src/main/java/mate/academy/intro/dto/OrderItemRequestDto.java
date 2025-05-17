package mate.academy.intro.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class OrderItemRequestDto {
    @NotNull
    private Long bookId;

    @NotNull
    @Positive
    private int quantity;
}
