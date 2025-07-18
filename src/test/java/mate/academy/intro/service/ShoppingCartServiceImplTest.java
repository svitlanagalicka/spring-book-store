package mate.academy.intro.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import mate.academy.intro.dto.CartItemRequestDto;
import mate.academy.intro.dto.ShoppingCartDto;
import mate.academy.intro.dto.UpdateCartItemDto;
import mate.academy.intro.exception.EntityNotFoundException;
import mate.academy.intro.mapper.ShoppingCartMapper;
import mate.academy.intro.model.Book;
import mate.academy.intro.model.CartItem;
import mate.academy.intro.model.ShoppingCart;
import mate.academy.intro.model.User;
import mate.academy.intro.repository.BookRepository;
import mate.academy.intro.repository.CartItemRepository;
import mate.academy.intro.repository.ShoppingCartRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceImplTest {
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private ShoppingCartMapper shoppingCartMapper;
    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;

    @Test
    @DisplayName("Create a shopping cart for the user")
    void createShoppingCartForUser_saveCartForUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@email.com");
        shoppingCartService.createShoppingCartForUser(user);
        verify(shoppingCartRepository).save(argThat(cart
                -> cart.getUser() != null && cart.getUser().equals(user)));
    }

    @Test
    @DisplayName("Return ShoppingCartDto when cart exists for user")
    void getCartByUser_whenCartExist() {
        Long userId = 1L;
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(3L);

        ShoppingCartDto shoppingCartDto = new ShoppingCartDto();
        shoppingCartDto.setId(3L);

        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.of(shoppingCart));
        when(shoppingCartMapper.toShoppingCartDto(shoppingCart)).thenReturn(shoppingCartDto);

        ShoppingCartDto result = shoppingCartService.getCartByUser(userId);

        assertEquals(shoppingCartDto, result);
        verify(shoppingCartRepository).findByUserId(userId);
        verify(shoppingCartMapper).toShoppingCartDto(shoppingCart);
    }

    @Test
    @DisplayName("Returns an error when the user's cart does not exist")
    void getCartByUser_whenCartNotExist() {
        Long userId = 999L;
        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.getCartByUser(userId));
        assertTrue(exception.getMessage().contains("Shopping cart not found for user:" + userId));
        verify(shoppingCartRepository).findByUserId(userId);
    }

    @Test
    @DisplayName("Successfully adds the item to the cart")
    void addItemToCart_addAndReturnDto_success() {
        Long bookId = 33L;
        CartItemRequestDto requestDto = new CartItemRequestDto();
        requestDto.setBookId(bookId);
        requestDto.setQuantity(3);
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(7L);
        Book book = new Book();
        book.setId(bookId);
        book.setTitle("Effective Java");
        ShoppingCartDto shoppingCartDto = new ShoppingCartDto();
        shoppingCartDto.setId(shoppingCart.getId());
        Long userId = 1L;

        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.of(shoppingCart));
        when(bookRepository.findByIdWithCategory(bookId)).thenReturn(Optional.of(book));
        when(shoppingCartMapper.toShoppingCartDto(shoppingCart)).thenReturn(shoppingCartDto);

        ShoppingCartDto result = shoppingCartService.addItemToCart(userId, requestDto);

        assertEquals(shoppingCartDto, result);
        verify(shoppingCartRepository).findByUserId(userId);
        verify(bookRepository).findByIdWithCategory(bookId);
        verify(cartItemRepository).save(any(CartItem.class));
        verify(shoppingCartMapper).toShoppingCartDto(shoppingCart);
    }

    @Test
    @DisplayName("Returns an error when the cart is not found")
    void addItemToCart_throwEntityNotFoundException_whenCartNotFound() {
        Long userId = 1L;
        CartItemRequestDto requestDto = new CartItemRequestDto();
        requestDto.setBookId(33L);
        requestDto.setQuantity(3);
        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.addItemToCart(userId, requestDto));
        assertTrue(exception.getMessage().contains("Shopping cart not found for user:" + userId));
        verify(shoppingCartRepository).findByUserId(userId);
    }

    @Test
    @DisplayName("The number of items in the cart has been successfully updated")
    void updateItemQuantity_updateQuantitySuccess() {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(100L);
        Long cartItemId = 77L;
        CartItem cartItem = new CartItem();
        cartItem.setId(cartItemId);
        cartItem.setQuantity(2);
        cartItem.setShoppingCart(shoppingCart);
        UpdateCartItemDto updateCartItemDto = new UpdateCartItemDto();
        updateCartItemDto.setQuantity(7);
        ShoppingCartDto expected = new ShoppingCartDto();
        expected.setId(shoppingCart.getId());
        Long userId = 1L;

        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.of(shoppingCart));
        when(cartItemRepository.findByIdAndShoppingCartId(cartItemId, shoppingCart.getId()))
                .thenReturn(Optional.of(cartItem));
        when(shoppingCartMapper.toShoppingCartDto(shoppingCart)).thenReturn(expected);
        ShoppingCartDto result = shoppingCartService
                .updateItemQuantity(userId, cartItemId, updateCartItemDto);
        assertEquals(expected, result);
        verify(shoppingCartRepository).findByUserId(userId);
        verify(cartItemRepository).findByIdAndShoppingCartId(cartItemId, shoppingCart.getId());
        verify(cartItemRepository).save(cartItem);
        verify(shoppingCartMapper).toShoppingCartDto(shoppingCart);
    }

    @Test
    @DisplayName("Returns an error if the product is not found in the user's cart")
    void updateItemQuantity_throwEntityNotFoundException_cartItemNotFound() {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(88L);
        UpdateCartItemDto updateCartItemDto = new UpdateCartItemDto();
        updateCartItemDto.setQuantity(7);
        Long userId = 1L;
        Long cartItemId = 999L;
        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.of(shoppingCart));
        when(cartItemRepository.findByIdAndShoppingCartId(cartItemId, shoppingCart.getId()))
                .thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                shoppingCartService.updateItemQuantity(userId, cartItemId, updateCartItemDto));

        assertTrue(exception.getMessage().contains("Cart item not found:" + cartItemId));
        verify(shoppingCartRepository).findByUserId(userId);
        verify(cartItemRepository).findByIdAndShoppingCartId(cartItemId, shoppingCart.getId());
    }

    @Test
    @DisplayName("Remove cart item successfully")
    void removeItem_deleteCartItem_success() {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(99L);
        Long cartItemId = 37L;
        Long userId = 1L;
        CartItem cartItem = new CartItem();
        cartItem.setId(cartItemId);
        cartItem.setShoppingCart(shoppingCart);
        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.of(shoppingCart));
        when(cartItemRepository.findByIdAndShoppingCartId(cartItemId, shoppingCart.getId()))
                .thenReturn(Optional.of(cartItem));

        shoppingCartService.removeItem(cartItemId, userId);

        verify(shoppingCartRepository).findByUserId(userId);
        verify(cartItemRepository).findByIdAndShoppingCartId(cartItemId, shoppingCart.getId());
        verify(cartItemRepository).delete(cartItem);
    }

    @Test
    @DisplayName("Returns an error when deleting an item when the cart is not found")
    void removeItem_throwEntityNotFoundException_shoppingCartNotFound() {
        Long userId = 1L;
        Long cartItemId = 77L;
        when(shoppingCartRepository.findByUserId(userId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> shoppingCartService.removeItem(cartItemId, userId));

        assertTrue(exception.getMessage().contains("Shopping cart not found for user:" + userId));
        verify(shoppingCartRepository).findByUserId(userId);
    }
}
