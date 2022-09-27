package com.ecommerce.backend.repository.custom;

import com.ecommerce.backend.domain.entity.Product;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.ecommerce.backend.domain.entity.QProduct.product;

@RequiredArgsConstructor
public class CustomProductRepositoryImpl implements CustomProductRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Product> findByIdList(List<Long> productIdList) {
        return queryFactory
                .select(product)
                .from(product)
                .where(product.id.in(productIdList))
                .fetch();
    }
}
