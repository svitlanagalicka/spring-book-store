package mate.academy.intro.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import mate.academy.intro.model.Status;

public class OrderResponseDto {
    private Long id;
    private String shippingAddress;
    private LocalDateTime orderDate;
    private Status status;
    private BigDecimal total;
    private List<OrderItemResponseDto> orderItems;
}
