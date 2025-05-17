package mate.academy.intro.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.intro.dto.CartItemDto;
import mate.academy.intro.dto.OrderItemResponseDto;
import mate.academy.intro.dto.OrderRequestDto;
import mate.academy.intro.dto.OrderResponseDto;
import mate.academy.intro.dto.ShoppingCartDto;
import mate.academy.intro.dto.UpdateOrderStatusRequestDto;
import mate.academy.intro.exception.EntityNotFoundException;
import mate.academy.intro.mapper.OrderItemMapper;
import mate.academy.intro.mapper.OrderMapper;
import mate.academy.intro.model.Book;
import mate.academy.intro.model.Order;
import mate.academy.intro.model.OrderItem;
import mate.academy.intro.model.Status;
import mate.academy.intro.model.User;
import mate.academy.intro.repository.BookRepository;
import mate.academy.intro.repository.OrderRepository;
import mate.academy.intro.repository.UserRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ShoppingCartService shoppingCartService;

    @Override
    public OrderResponseDto placeOrder(OrderRequestDto orderRequestDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found:" + userId));
        ShoppingCartDto shoppingCartDto = shoppingCartService.getCartByUser(userId);

        Order order = new Order();
        order.setUser(user);
        order.setStatus(Status.NEW);
        order.setOrderDate(LocalDateTime.now());
        order.setShippingAddress(orderRequestDto.getShippingAddress());
        order.setOrderItems(new HashSet<>());

        BigDecimal total = BigDecimal.ZERO;
        List<CartItemDto> cartItems = (List<CartItemDto>) shoppingCartDto.getCartItems();

        for (CartItemDto cartItemDto: cartItems) {
            Book book = bookRepository.findById(cartItemDto.getBookId())
                    .orElseThrow(() ->
                            new RuntimeException("Book not found:" + cartItemDto.getBookId()));
            OrderItem orderItem = new OrderItem();
            orderItem.setBook(book);
            orderItem.setQuantity(cartItemDto.getQuantity());
            orderItem.setPrice(book.getPrice());
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem);

            total = total.add(book.getPrice())
                    .multiply(BigDecimal.valueOf(cartItemDto.getQuantity()));
        }
        order.setTotal(total);
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toOrderDto(savedOrder);

    }

    @Override
    public List<OrderResponseDto> getOrderHistory(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(orderMapper::toOrderDto)
                .toList();
    }

    @Override
    public List<OrderItemResponseDto> getOrderItems(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found:" + orderId));
        return order.getOrderItems().stream()
                .map(orderItemMapper::toOrderItemDto).toList();
    }

    @Override
    public OrderItemResponseDto getOrderItemById(Long orderId, Long itemId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found:" + orderId));
        return order.getOrderItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .map(orderItemMapper::toOrderItemDto)
                .orElseThrow(() -> new RuntimeException("Order item not found:" + itemId));
    }

    @Override
    public OrderResponseDto updateOrderStatus(Long id,
                                              UpdateOrderStatusRequestDto
                                                      updateOrderStatusRequestDto) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id:" + id));
        order.setStatus(updateOrderStatusRequestDto.getStatus());
        orderRepository.save(order);
        return orderMapper.toOrderDto(order);
    }
}
