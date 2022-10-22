package com.ecommerce.backend.repository.jpa;

import com.ecommerce.backend.domain.entity.CartProduct;
import com.ecommerce.backend.repository.custom.CustomCartProductRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartProductRepository extends JpaRepository<CartProduct, Long>, CustomCartProductRepository {
    CartProduct findByCartIdAndProductId(Long cartId, Long productId);
}
