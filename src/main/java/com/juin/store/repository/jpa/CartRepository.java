package com.juin.store.repository.jpa;

import com.juin.store.domain.entity.Cart;
import com.juin.store.repository.querydsl.QuerydslCartRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long>, QuerydslCartRepository {
    Optional<Cart> findByAccountId(Long accountId);
}
