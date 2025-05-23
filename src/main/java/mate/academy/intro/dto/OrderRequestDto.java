package mate.academy.intro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Data;

@Data
public class OrderRequestDto {
    @NotBlank
    private String shippingAddress;

    @NotEmpty
    private List<OrderItemRequestDto> items;
}
