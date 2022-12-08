package com.ecommerce.backend.repository.jpa;

import com.ecommerce.backend.domain.entity.Cart;
import com.ecommerce.backend.repository.querydsl.QuerydslCartRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long>, QuerydslCartRepository {
    Optional<Cart> findByAccountId(Long accountId);
}
