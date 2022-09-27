package com.ecommerce.backend.repository.jpa;

import com.ecommerce.backend.domain.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value = "DELETE FROM orders WHERE account_id=:accountId"
    )
    void deleteByAccountId(@Param("accountId") Long accountId);

    List<Order> findByAccountId(Long accountId);

    @Query(nativeQuery = true,
            value = "SELECT o.order_date AS orderDate, " +
                    "op.order_count AS orderCount, " +
                    "p.product_id AS productId, " +
                    "p.price AS price, " +
                    "p.product_name AS productName, " +
                    "o.orders_id AS ordersId, " +
                    "op.order_product_id AS orderProductId, " +
                    "o.delivery_id AS deliveryId, " +
                    "o.order_status AS orderStatus " +
                    "FROM orders AS o " +
                    "JOIN order_product AS op ON o.orders_id = op.orders_id " +
                    "JOIN product AS p ON p.product_id = op.product_id " +
                    "WHERE o.account_id=:accountId"
    )
    List<OrderJoinResult> findOrderJoinOrderProductJoinProductByAccountId(@Param("accountId") Long accountId);
}
