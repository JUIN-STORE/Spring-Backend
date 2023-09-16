package store.juin.api.orderitem.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import store.juin.api.orderitem.model.entity.OrderItem;
import store.juin.api.orderitem.repository.querydsl.QuerydslOrderItemRepository;

import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long>, QuerydslOrderItemRepository {
    Optional<OrderItem> findByOrderId(Long oderId);
}
