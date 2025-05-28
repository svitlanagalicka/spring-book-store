package mate.academy.intro.dto;

import lombok.Data;

@Data
public class OrderItemResponseDto {
    private Long id;
    private int quantity;
    private Long bookId;
}
