package com.ecommerce.backend.repository.jpa;

import com.ecommerce.backend.domain.entity.ProductImage;
import com.ecommerce.backend.repository.querydsl.QuerydslProductImageRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long>, QuerydslProductImageRepository {
    List<ProductImage> findByProductId(Long productId);
}
