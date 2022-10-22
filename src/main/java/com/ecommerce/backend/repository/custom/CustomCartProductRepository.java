package com.ecommerce.backend.repository.custom;

import com.ecommerce.backend.domain.entity.CartProduct;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomCartProductRepository {
    Optional<List<CartProduct>> findByCartIdIn(Long cartId);

    Long deleteByCartIdAndProductId(Long cartId, Long productId);

    Optional<List<CartProduct>> findByCartIdListAndProductIdList(Long cartId, List<Long> productIdList);
}
