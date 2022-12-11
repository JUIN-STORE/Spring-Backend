package com.ecommerce.backend.repository.jpa;

import com.ecommerce.backend.domain.entity.OrderProduct;
import com.ecommerce.backend.repository.querydsl.QuerydslOrderProductRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderProductRepository extends JpaRepository<OrderProduct, Long>, QuerydslOrderProductRepository {
    Optional<OrderProduct> findByOrderId(Long oderId);
}
