package com.ecommerce.backend.repository.querydsl;

import com.ecommerce.backend.domain.entity.Product;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static com.ecommerce.backend.domain.entity.QProduct.product;

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
}
