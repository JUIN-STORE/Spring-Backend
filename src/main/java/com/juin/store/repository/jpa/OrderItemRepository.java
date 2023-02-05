package com.juin.store.repository.jpa;

import com.juin.store.domain.entity.OrderItem;
import com.juin.store.repository.querydsl.QuerydslOrderItemRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long>, QuerydslOrderItemRepository {
    Optional<OrderItem> findByOrderId(Long oderId);
}
