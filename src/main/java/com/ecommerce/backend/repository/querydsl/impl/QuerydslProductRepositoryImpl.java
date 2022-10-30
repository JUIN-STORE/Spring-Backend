package com.ecommerce.backend.repository.querydsl.impl;

import com.ecommerce.backend.domain.entity.Product;
import com.ecommerce.backend.repository.querydsl.ifs.QuerydslProductRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.ecommerce.backend.domain.entity.QProduct.product;

@RequiredArgsConstructor
public class QuerydslProductRepositoryImpl implements QuerydslProductRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Product> findByIdIn(List<Long> productIdList) {
        return queryFactory
                .select(product)
                .from(product)
                .where(product.id.in(productIdList))
                .fetch();
    }
}
