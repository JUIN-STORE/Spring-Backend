package com.ecommerce.backend.repository.custom;

import com.ecommerce.backend.domain.entity.CartProduct;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.ecommerce.backend.domain.entity.QCartProduct.cartProduct;

@RequiredArgsConstructor
public class CustomCartProductRepositoryImpl implements CustomCartProductRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<List<CartProduct>> findByCartIdIn(Long cartId) {
        return Optional.ofNullable(
                queryFactory
                    .select(cartProduct)
                    .from(cartProduct)
                    .where(cartProduct.cart.id.eq(cartId))
                    .fetch()
        );
    }

    @Override
    @Transactional
    public Long deleteByCartIdAndProductId(Long cartId, Long productId) {
        return queryFactory
                .delete(cartProduct)
                .where(cartProduct.cart.id.eq(cartId).and(cartProduct.product.id.eq(productId)))
                .execute();
    }

    @Override
    public Optional<List<CartProduct>> findByCartIdListAndProductIdList(Long cartId, List<Long> productIdList) {
        return Optional.ofNullable(
                queryFactory
                        .select(cartProduct)
                        .from(cartProduct)
                        .where(cartProduct.cart.id.eq(cartId).and(cartProduct.product.id.in(productIdList)))
                        .fetch()
        );
    }
}
