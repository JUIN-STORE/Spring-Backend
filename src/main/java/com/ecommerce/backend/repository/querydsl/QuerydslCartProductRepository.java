package com.ecommerce.backend.repository.querydsl;

import com.ecommerce.backend.domain.entity.CartProduct;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuerydslCartProductRepository {
    Optional<List<CartProduct>> findByAccountId(Long accountId);

    Optional<List<CartProduct>> findByCartId(Long cartId);

    long deleteByCartIdAndProductId(Long cartId, Long productId);

    Optional<List<CartProduct>> findByCartIdAndProductIdIn(Long cartId, List<Long> productIdList);
}
