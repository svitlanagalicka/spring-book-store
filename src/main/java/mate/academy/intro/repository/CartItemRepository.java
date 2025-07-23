package mate.academy.intro.repository;

import java.util.Optional;
import mate.academy.intro.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByIdAndShoppingCartId(Long id, Long shoppingCartId);
}
