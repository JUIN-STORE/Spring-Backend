package store.juin.api.orderitem.repository.querydsl.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import store.juin.api.orderitem.repository.querydsl.QuerydslOrderItemRepository;

import java.util.List;

import static store.juin.api.common.entity.QOrderItem.orderItem;


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
