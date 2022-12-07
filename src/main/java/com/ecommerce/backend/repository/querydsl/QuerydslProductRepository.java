package com.ecommerce.backend.repository.querydsl;

import com.ecommerce.backend.domain.entity.Product;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuerydslProductRepository {
    Optional<List<Product>> findByIdIn(List<Long> productIdList);
}
