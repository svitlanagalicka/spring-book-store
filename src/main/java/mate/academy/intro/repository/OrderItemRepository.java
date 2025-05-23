package mate.academy.intro.repository;

import java.util.Optional;
import mate.academy.intro.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("""
            SELECT oi FROM OrderItem oi WHERE oi.id = :itemId 
            AND oi.order.id = :orderId 
            AND oi.order.user.id = :userId""")
    Optional<OrderItem> findByIdAndOrderIdAndUserId(Long itemId, Long orderId, Long userId);
}
