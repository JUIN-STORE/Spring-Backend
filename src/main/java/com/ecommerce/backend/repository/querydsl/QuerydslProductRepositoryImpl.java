package com.ecommerce.backend.repository.querydsl;

import com.ecommerce.backend.domain.entity.Product;
import com.ecommerce.backend.domain.response.CartProductResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static com.ecommerce.backend.domain.entity.QCartProduct.cartProduct;
import static com.ecommerce.backend.domain.entity.QProduct.product;
import static com.ecommerce.backend.domain.entity.QProductImage.productImage;

@RequiredArgsConstructor
public class QuerydslProductRepositoryImpl implements QuerydslProductRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<List<Product>> findByIdIn(List<Long> productIdList) {
        return Optional.ofNullable(
                queryFactory
                        .select(product)
                        .from(product)
                        .where(product.id.in(productIdList))
                        .fetch()
        );
    }

    @Override
    public Optional<List<CartProductResponse.Read>> findAllByProductIdAndProductImageIdAndThumbnail(List<Long> productIdList) {
        return Optional.ofNullable(
                queryFactory
                        .select(Projections.fields(CartProductResponse.Read.class
                                , product.id.as("productId")
                                , product.productName
                                , product.price
                                , product.description
                                , productImage.imageName
                                , productImage.originImageName
                                , productImage.imageUrl
                                , productImage.thumbnail))
                        .from(product)
                        .join(productImage)
                        .on(product.id.eq(productImage.product.id))
                        .join(cartProduct)
                        .on(cartProduct.product.id.eq(product.id))
                        .where(product.id.in(productIdList))
                        .where(productImage.thumbnail.eq(true))
                        .fetch()
        );
    }
}