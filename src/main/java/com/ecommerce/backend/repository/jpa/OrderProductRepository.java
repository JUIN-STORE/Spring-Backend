package com.ecommerce.backend.repository.jpa;

import com.ecommerce.backend.domain.entity.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {
    Optional<OrderProduct> findByOrderId(Long oderId);

    // querydsl에는 delete join 기능이 없어서 네이티브 사용
    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value = "DELETE order_product " +
                    "FROM orders " +
                    "LEFT JOIN order_product ON orders.orders_id = order_product.orders_id " +
                    "WHERE orders.orders_id IN (:orderIdList)"
    )
    void deleteByOrderIdList(@Param("orderIdList") List<Long> orderIdList);
}
