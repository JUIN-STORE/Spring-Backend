package com.ecommerce.backend.repository.custom;

import com.ecommerce.backend.domain.entity.ProductImage;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomProductImageRepository {
    Optional<List<ProductImage>> findAllByProductId(List<Long> productIdList);
}
