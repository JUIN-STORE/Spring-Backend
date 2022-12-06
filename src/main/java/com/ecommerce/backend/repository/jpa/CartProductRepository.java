package com.ecommerce.backend.repository.jpa;

import com.ecommerce.backend.domain.entity.CartProduct;
import com.ecommerce.backend.repository.querydsl.QuerydslCartProductRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@EnableJpaRepositories
public interface CartProductRepository extends JpaRepository<CartProduct, Long>, QuerydslCartProductRepository {
    CartProduct findByCartIdAndProductId(Long cartId, Long productId);

    // ref) https://extbrain.tistory.com/74
    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value = "DELETE cart_product " +
                    "FROM cart " +
                    "LEFT JOIN cart_product ON cart.cart_id = cart_product.cart_id " +
                    "WHERE cart.account_id=:accountId")
    void deleteByAccountId(@Param("accountId") Long accountId);
}
