package mate.academy.intro.controller;

import lombok.RequiredArgsConstructor;
import mate.academy.intro.dto.CartItemRequestDto;
import mate.academy.intro.dto.ShoppingCartDto;
import mate.academy.intro.service.ShoppingCartService;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/cart ")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @GetMapping
    public ShoppingCartDto getShoppingCart(@RequestParam String email) {
        return shoppingCartService.getCartByUser(email);
    }

    @PostMapping("api/cart/items/{cartItemId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addItemToCart(@RequestParam String email,
                              @RequestBody CartItemRequestDto cartItemRequestDto) {
        shoppingCartService.addItemToCart(email, cartItemRequestDto);
    }

    @PutMapping("/api/cart/items/{cartItemId}")
    @ResponseStatus(HttpStatus.OK)
    public void updateItemQuantity(@PathVariable Long cartItemId,
                                   @RequestParam Long shoppingCartId,
                                   @RequestParam Long bookId,
                                   @RequestParam int quantity) {
        shoppingCartService.updateItemQuantity(cartItemId, shoppingCartId, bookId, quantity);
    }

    @DeleteMapping("/api/cart/items/{cartItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@PathVariable Long cartItemId) {
        shoppingCartService.removeItem(cartItemId);
    }
}
