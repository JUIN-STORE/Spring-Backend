package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.Category;
import com.ecommerce.backend.domain.entity.Product;
import com.ecommerce.backend.domain.entity.ProductCategory;
import com.ecommerce.backend.repository.jpa.ProductCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductCategoryService {
    private final ProductCategoryRepository productCategoryRepository;

    public void add(Product product, Category category) {
        final ProductCategory productCategory = new ProductCategory(product, category);
        productCategoryRepository.save(productCategory);
    }
}
