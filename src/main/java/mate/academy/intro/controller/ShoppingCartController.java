package mate.academy.intro.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.intro.dto.CartItemRequestDto;
import mate.academy.intro.dto.ShoppingCartDto;
import mate.academy.intro.dto.UpdateCartItemDto;
import mate.academy.intro.service.ShoppingCartService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @GetMapping
    public ShoppingCartDto getShoppingCart(@RequestParam String email) {
        return shoppingCartService.getCartByUser(email);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingCartDto addItemToCart(Authentication authentication,
                              @RequestBody @Valid CartItemRequestDto cartItemRequestDto) {
        String email = authentication.getName();
        return shoppingCartService.addItemToCart(email, cartItemRequestDto);
    }

    @PutMapping("/cart-items/{cartItemId}")
    @ResponseStatus(HttpStatus.OK)
    public void updateItemQuantity(Authentication authentication,
                                   @PathVariable Long cartItemId,
                                   @RequestBody @Valid UpdateCartItemDto item) {
        shoppingCartService.updateItemQuantity(authentication.getName(),
                cartItemId,
                item.getShoppingCartId(),
                item.getBookId(),
                item.getQuantity());
    }

    @DeleteMapping("/cart-items/{cartItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(Authentication authentication,
                           @PathVariable Long cartItemId) {
        shoppingCartService.removeItem(cartItemId, authentication.getName());
    }
}
