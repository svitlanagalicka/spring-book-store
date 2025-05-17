package mate.academy.intro.service;

import java.util.List;
import mate.academy.intro.dto.OrderItemResponseDto;
import mate.academy.intro.dto.OrderRequestDto;
import mate.academy.intro.dto.OrderResponseDto;
import mate.academy.intro.dto.UpdateOrderStatusRequestDto;

public interface OrderService {

    OrderResponseDto placeOrder(OrderRequestDto requestDto, Long userId);

    List<OrderResponseDto> getOrderHistory(Long userId);

    List<OrderItemResponseDto> getOrderItems(Long orderId, Long userId);

    OrderItemResponseDto getOrderItemById(Long orderId, Long itemId, Long userId);

    OrderResponseDto updateOrderStatus(Long id,
                                       UpdateOrderStatusRequestDto updateOrderStatusRequestDto);
}
