package com.ecommerce.backend.repository.querydsl;

import com.ecommerce.backend.domain.entity.Product;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuerydslProductRepository {
    List<Product> findByIdIn(List<Long> productIdList);
}
