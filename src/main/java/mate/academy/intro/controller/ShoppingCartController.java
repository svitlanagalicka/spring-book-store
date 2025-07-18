package mate.academy.intro.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.intro.dto.CartItemRequestDto;
import mate.academy.intro.dto.ShoppingCartDto;
import mate.academy.intro.dto.UpdateCartItemDto;
import mate.academy.intro.model.User;
import mate.academy.intro.service.ShoppingCartService;
import mate.academy.intro.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get shopping cart", description = "Get cart for user")
    public ShoppingCartDto getShoppingCart(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.getCartByUser(user.getId());
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add book to cart", description = "Add book to the shopping cart")
    public ShoppingCartDto addItemToCart(Authentication authentication,
                              @RequestBody @Valid CartItemRequestDto cartItemRequestDto) {
        String email = authentication.getName();
        Long userId = userService.findIdByEmail(email);
        return shoppingCartService.addItemToCart(userId, cartItemRequestDto);
    }

    @PutMapping("/cart-items/{cartItemId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Update item",
                  description = "Update the number of books in the shopping cart")
    public ShoppingCartDto updateItemQuantity(Authentication authentication,
                                   @PathVariable Long cartItemId,
                                   @RequestBody @Valid UpdateCartItemDto updateCartItemDto) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.updateItemQuantity(user.getId(),
                cartItemId,
                updateCartItemDto);
    }

    @DeleteMapping("/cart-items/{cartItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Delete item", description = "Remove a book from the shopping cart")
    public void deleteItem(Authentication authentication,
                           @PathVariable Long cartItemId) {
        User user = (User) authentication.getPrincipal();
        shoppingCartService.removeItem(cartItemId, user.getId());
    }
}
