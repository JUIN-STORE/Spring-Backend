package com.ecommerce.backend.repository.querydsl.ifs;

import com.ecommerce.backend.domain.entity.CartProduct;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuerydslCartProductRepository {
    Optional<List<CartProduct>> findByCartId(Long cartId);

    Long deleteByCartIdAndProductId(Long cartId, Long productId);

    Optional<List<CartProduct>> findByCartIdAndProductIdIn(Long cartId, List<Long> productIdList);
}