package com.ecommerce.backend.repository.jpa;

import com.ecommerce.backend.domain.entity.OrderItem;
import com.ecommerce.backend.repository.querydsl.QuerydslOrderItemRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long>, QuerydslOrderItemRepository {
    Optional<OrderItem> findByOrderId(Long oderId);
}
