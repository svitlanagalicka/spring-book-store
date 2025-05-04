package mate.academy.intro.service;

import mate.academy.intro.dto.CartItemRequestDto;
import mate.academy.intro.dto.ShoppingCartDto;

public interface ShoppingCartService {

    ShoppingCartDto getCartByUser(String email);

    void addItemToCart(String username, CartItemRequestDto cartItemRequestDto);

    void updateItemQuantity(Long cartItemId, Long shoppingCartId, Long bookId, int quantity);

    void removeItem(Long cartItemId);

    void clearCart(String email);
}
