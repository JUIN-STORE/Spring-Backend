package com.ecommerce.backend.repository.jpa;

import com.ecommerce.backend.domain.entity.Product;
import com.ecommerce.backend.repository.custom.CustomProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, CustomProductRepository {
    Page<Product> findAll(Pageable pageable);

//    @Query(nativeQuery = true,
//            value = "SELECT * " +
//                    "FROM product " +
//                    "WHERE product_id IN (:productIdList)"
//    )
//    List<Product> findByIdList(@Param("productIdList") List<Long> productIdList);

    List<Product> findByIdList(List<Long> productIdList);

    // 참고 https://yonguri.tistory.com/122
    Page<Product> findByProductNameContaining(Pageable pageable, String productName);

    Long countByProductNameContaining(String productName);
}
