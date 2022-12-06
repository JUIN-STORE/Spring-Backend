package com.ecommerce.backend.repository.jpa;

import com.ecommerce.backend.domain.entity.Product;
import com.ecommerce.backend.repository.querydsl.QuerydslProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, QuerydslProductRepository {
    Page<Product> findAll(Pageable pageable);

    Page<Product> findByCategoryId(Pageable pageable, Long category);

    // 참고 https://yonguri.tistory.com/122
    Page<Product> findByProductNameContaining(Pageable pageable, String productName);

    Page<Product> findByProductNameContainingAndCategoryId(Pageable pageable, String productName, Long categoryId);

    Long countByProductNameContaining(String productName);
}
