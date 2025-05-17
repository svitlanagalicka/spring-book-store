package mate.academy.intro.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class OrderResponseDto {
    private String shippingAddress;
    private BigDecimal total;
    private String status;
    private LocalDateTime orderDate;
    private List<OrderItemResponseDto> items;

}
