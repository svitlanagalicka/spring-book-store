package mate.academy.intro.service;

import mate.academy.intro.dto.CartItemRequestDto;
import mate.academy.intro.dto.ShoppingCartDto;
import mate.academy.intro.model.User;

public interface ShoppingCartService {

    public void createShoppingCartForUser(User user);

    ShoppingCartDto getCartByUser(String email);

    ShoppingCartDto addItemToCart(String username, CartItemRequestDto cartItemRequestDto);

    void updateItemQuantity(String email, Long cartItemId,
                            Long shoppingCartId, Long bookId, int quantity);

    void removeItem(Long cartItemId, String email);
}
