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
                    "WHERE product_id IN (:productId)"
    )
    List<Product> findByIdIn(@Param("productId") List<Long> productId);
}
