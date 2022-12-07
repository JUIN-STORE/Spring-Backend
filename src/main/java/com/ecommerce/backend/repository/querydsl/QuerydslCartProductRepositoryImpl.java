package com.ecommerce.backend.repository.querydsl;

import com.ecommerce.backend.domain.entity.CartProduct;
import com.ecommerce.backend.domain.response.CartProductResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.ecommerce.backend.domain.entity.QCartProduct.cartProduct;
import static com.ecommerce.backend.domain.entity.QProduct.product;
import static com.ecommerce.backend.domain.entity.QProductImage.productImage;

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

    @Override
    public Optional<List<CartProductResponse.Read>>
    findAllByCartIdAndProductIdListAndThumbnail(Long cartId, List<Long> productIdList, boolean isThumbnail) {

        return Optional.ofNullable(
                queryFactory
                        .select(Projections.fields(CartProductResponse.Read.class
                                , product.id.as("productId")
                                , product.productName
                                , product.price
                                , cartProduct.count
                                , product.description
                                , productImage.imageName
                                , productImage.originImageName
                                , productImage.imageUrl
                                , productImage.thumbnail))
                        .from(cartProduct)
                        .join(product)
                        .on(cartProduct.product.id.eq(product.id))
                        .join(productImage)
                        .on(productImage.product.id.eq(product.id))
                        .where(cartProduct.cart.id.eq(cartId))
                        .where(product.id.in(productIdList))
                        .where(productImage.thumbnail.eq(isThumbnail))
                        .fetch()
        );
    }
}
