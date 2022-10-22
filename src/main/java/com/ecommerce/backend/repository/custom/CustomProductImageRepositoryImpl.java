package com.ecommerce.backend.repository.custom;

import com.ecommerce.backend.domain.entity.ProductImage;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static com.ecommerce.backend.domain.entity.QProductImage.productImage;

@RequiredArgsConstructor
public class CustomProductImageRepositoryImpl implements CustomProductImageRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<List<ProductImage>> findAllByProductId(List<Long> productIdList) {
        return Optional.ofNullable(
                queryFactory
                        .select(productImage)
                        .from(productImage)
                        .where(productImage.product.id.in(productIdList))
                        .fetch()
        );
    }
}
