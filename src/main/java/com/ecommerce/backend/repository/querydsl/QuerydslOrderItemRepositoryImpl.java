package com.ecommerce.backend.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.ecommerce.backend.domain.entity.QOrderItem.orderItem;


@RequiredArgsConstructor
public class QuerydslOrderItemRepositoryImpl implements QuerydslOrderItemRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public long deleteByOrderIdList(List<Long> orderIdList) {
        return queryFactory
                .delete(orderItem)
                .where(orderItem.order.id.in(orderIdList))
                .execute();
    }
}
