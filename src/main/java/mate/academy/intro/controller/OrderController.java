package mate.academy.intro.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.intro.dto.OrderItemResponseDto;
import mate.academy.intro.dto.OrderRequestDto;
import mate.academy.intro.dto.OrderResponseDto;
import mate.academy.intro.dto.UpdateOrderStatusRequestDto;
import mate.academy.intro.model.User;
import mate.academy.intro.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Place an order",
            description = "Place an order based on items in the shopping cart")
    public OrderResponseDto placeOrder(@Valid @RequestBody OrderRequestDto orderRequestDto,
                                       Authentication authentication) {
        User userId = (User) authentication.getPrincipal();
        return orderService.placeOrder(orderRequestDto, userId.getId());
    }

    @GetMapping("/order")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get all orders",
            description = "Get a list of all available orders")
    public Page<OrderResponseDto> getAll(Pageable pageable) {
        return orderService.findAll(pageable);
    }

    @GetMapping("/order/history")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get order history",
            description = "Returns a list of all orders placed by the user")
    public Page<OrderResponseDto> getOrderHistory(Authentication authentication,
                                                  Pageable pageable) {
        User userId = (User) authentication.getPrincipal();
        return orderService.getOrderHistory(userId.getId(), pageable);
    }

    @GetMapping("/{orderId}/items")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get items from a specific order",
            description = "Returns a list of items")
    public List<OrderItemResponseDto> getOrderItems(@PathVariable Long orderId,
                                                    Authentication authentication) {
        User userId = (User) authentication.getPrincipal();
        return orderService.getOrderItems(orderId, userId.getId());
    }

    @GetMapping("/{orderId}/items/{itemId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get specific item from order",
            description = "Returns detailed information about item in a user's order")
    public OrderItemResponseDto getOrderItemById(@PathVariable Long orderId,
                                                 @PathVariable Long itemId,
                                                 Authentication authentication) {
        User userId = (User) authentication.getPrincipal();
        return orderService.getOrderItemById(orderId, itemId, userId.getId());
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update order status",
            description = "Allows an admin to update the status of an order")
    public OrderResponseDto updateOrderStatus(@PathVariable Long id,
                                              @Valid @RequestBody UpdateOrderStatusRequestDto
                                                      updateOrderStatusRequestDto) {
        return orderService.updateOrderStatus(id, updateOrderStatusRequestDto);
    }
}
