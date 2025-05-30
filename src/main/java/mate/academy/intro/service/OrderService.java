package mate.academy.intro.service;

import java.util.List;
import mate.academy.intro.dto.OrderItemResponseDto;
import mate.academy.intro.dto.OrderRequestDto;
import mate.academy.intro.dto.OrderResponseDto;
import mate.academy.intro.dto.UpdateOrderStatusRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    Page<OrderResponseDto> findAll(Pageable pageable);

    OrderResponseDto placeOrder(OrderRequestDto requestDto, Long userId);

    Page<OrderResponseDto> getOrderHistory(Long userId, Pageable pageable);

    List<OrderItemResponseDto> getOrderItems(Long orderId, Long userId);

    OrderItemResponseDto getOrderItemById(Long orderId, Long itemId, Long userId);

    OrderResponseDto updateOrderStatus(Long id,
                                       UpdateOrderStatusRequestDto updateOrderStatusRequestDto);
}
