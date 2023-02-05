package store.juin.api.repository.jpa;

import store.juin.api.domain.entity.OrderItem;
import store.juin.api.repository.querydsl.QuerydslOrderItemRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long>, QuerydslOrderItemRepository {
    Optional<OrderItem> findByOrderId(Long oderId);
}
