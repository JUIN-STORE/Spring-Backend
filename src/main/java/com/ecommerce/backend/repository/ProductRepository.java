package com.ecommerce.backend.repository;

import com.ecommerce.backend.domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, QuerydslPredicateExecutor<Product> {
    List<Product> findByProductName(String productName);
    List<Product> findByProductNameOrDescription(String productName, String description);
    List<Product> findByPriceLessThan(Integer price);
    List<Product> findByPriceLessThanOrderByPriceDesc(Integer price);

    @Query(value = "select * from Product p where p.description like %:description% order by p.price desc", nativeQuery = true)
    List<Product> findByProductDescription(@Param("description") String description);
}
