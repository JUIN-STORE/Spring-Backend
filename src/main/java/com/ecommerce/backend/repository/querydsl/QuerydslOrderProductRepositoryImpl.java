package com.ecommerce.backend.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.ecommerce.backend.domain.entity.QOrderProduct.orderProduct;


@RequiredArgsConstructor
public class QuerydslOrderProductRepositoryImpl implements QuerydslOrderProductRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public long deleteByOrderIdList(List<Long> orderIdList) {
        return queryFactory
                .delete(orderProduct)
                .where(orderProduct.order.id.in(orderIdList))
                .execute();
    }
}
