package mate.academy.intro.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class OrderItemResponseDto {
    private String bookTitle;
    private int quantity;
    private BigDecimal price;
}
