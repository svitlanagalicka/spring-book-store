package mate.academy.intro.service;

import jakarta.validation.Valid;
import mate.academy.intro.dto.CartItemRequestDto;
import mate.academy.intro.dto.ShoppingCartDto;
import mate.academy.intro.dto.UpdateCartItemDto;
import mate.academy.intro.model.User;

public interface ShoppingCartService {

    public void createShoppingCartForUser(User user);

    ShoppingCartDto getCartByUser(Long userId);

    ShoppingCartDto addItemToCart(Long userId, CartItemRequestDto cartItemRequestDto);

    ShoppingCartDto updateItemQuantity(Long userId, Long cartItemId,
                                       @Valid UpdateCartItemDto quantity);

    void removeItem(Long cartItemId, Long userId);
}
