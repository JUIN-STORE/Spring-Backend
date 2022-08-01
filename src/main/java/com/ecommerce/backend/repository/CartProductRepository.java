package com.ecommerce.backend.repository;

import com.ecommerce.backend.domain.entity.CartProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CartProductRepository extends JpaRepository<CartProduct, Long> {
    CartProduct findByCartIdAndProductId(Long cartId, Long productId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM cart_product WHERE cart_id = :cartId and product_id = :productId", nativeQuery = true)
    int deleteByCartIdAndProductId(@Param("cartId") Long cartId, @Param("productId") Long productId);
}
