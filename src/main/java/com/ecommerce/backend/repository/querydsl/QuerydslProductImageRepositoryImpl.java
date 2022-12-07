package com.ecommerce.backend.repository.querydsl;

import com.ecommerce.backend.domain.entity.ProductImage;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static com.ecommerce.backend.domain.entity.QProductImage.productImage;

@RequiredArgsConstructor
public class QuerydslProductImageRepositoryImpl implements QuerydslProductImageRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<List<ProductImage>> findByThumbnail(boolean isThumbnail) {
        return Optional.ofNullable(
                queryFactory.selectFrom(productImage)
                        .where(productImage.thumbnail.eq(isThumbnail))
                        .fetch()
        );
    }

    @Override
    public Optional<List<ProductImage>> findAllByProductIdIn(List<Long> productIdList) {
        return Optional.ofNullable(
                queryFactory
                        .select(productImage)
                        .from(productImage)
                        .where(productImage.product.id.in(productIdList))
                        .fetch()
        );
    }

    @Override
    public long deleteAll(List<ProductImage> productImageList) {
        return queryFactory
                .delete(productImage)
                .where(productImage.in(productImageList))
                .execute();
    }
}
