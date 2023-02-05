package store.juin.api.repository.jpa;

import store.juin.api.domain.entity.Order;
import store.juin.api.repository.querydsl.QuerydslOrderRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, QuerydslOrderRepository {
    Optional<List<Order>> findAllByAccountId(Long accountId);

    Optional<Order> findByIdAndAccountId(Long orderId, Long accountId);

//    @Query(nativeQuery = true,
//            value = "SELECT o.order_date AS orderDate, " +
//                    "op.order_count AS orderCount, " +
//                    "p.item_id AS itemId, " +
//                    "p.price AS price, " +
//                    "p.item_name AS name, " +
//                    "o.orders_id AS ordersId, " +
//                    "op.order_item_id AS orderItemId, " +
//                    "o.delivery_id AS deliveryId, " +
//                    "o.order_status AS orderStatus " +
//                    "FROM orders AS o " +
//                    "JOIN order_item AS op ON o.orders_id = op.orders_id " +
//                    "JOIN item AS p ON p.item_id = op.item_id " +
//                    "WHERE o.account_id=:accountId"
//    )
//    List<OrderJoinResult> findOrderJoinOrderItemJoinItemByAccountId(@Param("accountId") Long accountId);
}
