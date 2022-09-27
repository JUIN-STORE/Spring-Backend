package com.ecommerce.backend.repository.jpa;

import com.ecommerce.backend.domain.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByAccountId(Long accountId);

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
