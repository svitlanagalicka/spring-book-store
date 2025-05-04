package mate.academy.intro.service;

import lombok.RequiredArgsConstructor;
import mate.academy.intro.dto.CartItemRequestDto;
import mate.academy.intro.dto.ShoppingCartDto;
import mate.academy.intro.exception.EntityNotFoundException;
import mate.academy.intro.mapper.ShoppingCartMapper;
import mate.academy.intro.model.Book;
import mate.academy.intro.model.CartItem;
import mate.academy.intro.model.ShoppingCart;
import mate.academy.intro.model.User;
import mate.academy.intro.repository.BookRepository;
import mate.academy.intro.repository.CartItemRepository;
import mate.academy.intro.repository.ShoppingCartRepository;
import mate.academy.intro.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final ShoppingCartMapper shoppingCartMapper;

    @Override
    public ShoppingCartDto getCartByUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(user.getId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Shopping cart not found for user:" + email));
        return shoppingCartMapper.toShoppingCartDto(shoppingCart);
    }

    @Override
    @Transactional
    public void addItemToCart(String email, CartItemRequestDto cartItemRequestDto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(user.getId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Shopping cart not found for user:" + email));
        Book book = bookRepository.findByIdWithCategory(cartItemRequestDto.getBookId())
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));
        CartItem cartItem = new CartItem();
        cartItem.setBook(book);
        cartItem.setQuantity(cartItemRequestDto.getQuantity());
        cartItem.setShoppingCart(shoppingCart);
        cartItemRepository.save(cartItem);

    }

    @Override
    @Transactional
    public void updateItemQuantity(Long cartItemId, Long shoppingCartId,
                                   Long bookId, int quantity) {
        CartItem cartItem = cartItemRepository.findByShoppingCartIdAndBookId(shoppingCartId, bookId)
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found"));
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);

    }

    @Override
    public void removeItem(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found"));
        cartItemRepository.delete(cartItem);

    }

    @Override
    public void clearCart(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(user.getId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Shopping cart not found for user" + email));
        cartItemRepository.deleteByShoppingCartId(shoppingCart.getId());

    }
}
