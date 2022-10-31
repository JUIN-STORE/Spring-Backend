package com.ecommerce.backend.repository.querydsl;

import com.ecommerce.backend.domain.entity.ProductImage;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuerydslProductImageRepository {
    Optional<List<ProductImage>> findAllByProductIdIn(List<Long> productIdList);
}
