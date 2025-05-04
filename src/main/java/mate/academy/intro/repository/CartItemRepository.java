package mate.academy.intro.repository;

import java.util.List;
import java.util.Optional;
import mate.academy.intro.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByShoppingCartId(Long shoppingCartId);

    Optional<CartItem> findByShoppingCartIdAndBookId(Long shoppingCartId, Long bookId);

    void deleteByShoppingCartId(Long shoppingCartId);
}
