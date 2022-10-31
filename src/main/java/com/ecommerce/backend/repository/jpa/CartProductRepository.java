package com.ecommerce.backend.repository.jpa;

import com.ecommerce.backend.domain.entity.CartProduct;
import com.ecommerce.backend.repository.querydsl.QuerydslCartProductRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@Repository
@EnableJpaRepositories
public interface CartProductRepository
        extends JpaRepository<CartProduct, Long>, QuerydslCartProductRepository {
    CartProduct findByCartIdAndProductId(Long cartId, Long productId);
}
