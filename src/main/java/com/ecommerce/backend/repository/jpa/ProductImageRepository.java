package com.ecommerce.backend.repository.jpa;

import com.ecommerce.backend.domain.entity.ProductImage;
import com.ecommerce.backend.repository.custom.CustomProductImageRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long>, CustomProductImageRepository {
    List<ProductImage> findByProductId(Long productId);
}
