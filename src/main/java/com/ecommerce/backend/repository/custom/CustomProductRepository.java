package com.ecommerce.backend.repository.custom;

import com.ecommerce.backend.domain.entity.Product;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomProductRepository {
    List<Product> findByIdList(List<Long> productIdList);
}
