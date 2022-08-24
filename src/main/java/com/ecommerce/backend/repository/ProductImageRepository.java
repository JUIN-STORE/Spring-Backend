package com.ecommerce.backend.repository;

import com.ecommerce.backend.domain.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long>{
    List<ProductImage> findByProductId(Long productId);

    @Query(nativeQuery = true,
            value = "SELECT * " +
                    "FROM product_image " +
                    "WHERE product_id IN (:productIdList)"
    )
    List<ProductImage> findAllByProductId(@Param("productIdList") List<Long> productIdList);
}
