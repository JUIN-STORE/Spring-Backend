package com.ecommerce.backend.repository.querydsl;

import com.ecommerce.backend.domain.entity.CartProduct;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.ecommerce.backend.domain.entity.QCartProduct.cartProduct;

@RequiredArgsConstructor
public class QuerydslCartProductRepositoryImpl implements QuerydslCartProductRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<List<CartProduct>> findByAccountId(Long accountId) {
        return null;
    }

    @Override
    public Optional<List<CartProduct>> findByCartId(Long cartId) {
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
    public long deleteByCartIdAndProductId(Long cartId, Long productId) {
        return queryFactory
                .delete(cartProduct)
                .where(cartProduct.cart.id.eq(cartId).and(cartProduct.product.id.eq(productId)))
                .execute();
    }

    @Override
    public Optional<List<CartProduct>> findByCartIdAndProductIdIn(Long cartId, List<Long> productIdList) {
        return Optional.ofNullable(
                queryFactory
                        .select(cartProduct)
                        .from(cartProduct)
                        .where(cartProduct.cart.id.eq(cartId).and(cartProduct.product.id.in(productIdList)))
                        .fetch()
        );
    }
}
