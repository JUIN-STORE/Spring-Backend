package com.ecommerce.backend.repository;

import com.ecommerce.backend.domain.entity.CartProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartProductRepository extends JpaRepository<CartProduct, Long> {

    @Query(nativeQuery = true,
            value = "SELECT * " +
                    "FROM cart_product " +
                    "WHERE cart_id=:cartId"
    )
    Optional<List<CartProduct>> findByCartIdIn(@Param("cartId") Long cartId);

    CartProduct findByCartIdAndProductId(Long cartId, Long productId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM cart_product WHERE cart_id = :cartId and product_id = :productId", nativeQuery = true)
    int deleteByCartIdAndProductId(@Param("cartId") Long cartId, @Param("productId") Long productId);

    @Query(nativeQuery = true,
            value = "SELECT * " +
                    "FROM cart_product " +
                    "WHERE cart_id = :cartId AND product_id IN (:productIdList)"
    )
    List<CartProduct> findByCartIdListAndProductIdList(@Param("cartId") Long cartId,
                                                   @Param("productIdList") List<Long> productIdList);
}
