package mate.academy.intro.dto;

import java.util.Set;
import lombok.Data;

@Data
public class ShoppingCartRequestDto {
    private Long userId;
    private Set<CartItemRequestDto> cartItems;
}
