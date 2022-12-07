package com.ecommerce.backend.repository.querydsl;

import com.ecommerce.backend.domain.entity.Product;
import com.ecommerce.backend.domain.response.CartProductResponse;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuerydslProductRepository {
    Optional<List<Product>> findByIdIn(List<Long> productIdList);

    Optional<List<CartProductResponse.Read>> findAllByProductIdAndProductImageIdAndThumbnail(List<Long> productIdList);
}
