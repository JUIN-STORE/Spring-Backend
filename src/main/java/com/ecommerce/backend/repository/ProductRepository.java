package com.ecommerce.backend.repository;

import com.ecommerce.backend.domain.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{
    Page<Product> findAll(Pageable pageable);

    @Query(nativeQuery = true,
            value = "SELECT * " +
                    "FROM product " +
                    "WHERE product_id IN (:productIdList)"
    )
    List<Product> findByIdList(@Param("productIdList") List<Long> productIdList);

    // 참고 https://yonguri.tistory.com/122
    Page<Product> findByProductNameContaining(Pageable pageable, String productName);

    Long countByProductNameContaining(String productName);
}
