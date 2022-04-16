package com.ecommerce.backend.repository;

import com.ecommerce.backend.domain.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long>{
    List<ProductImage> findByProductIdOrderById(Long productId);
}
