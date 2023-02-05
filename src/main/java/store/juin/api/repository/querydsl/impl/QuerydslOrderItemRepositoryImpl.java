package store.juin.api.repository.querydsl.impl;

import store.juin.api.repository.querydsl.QuerydslOrderItemRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static store.juin.api.domain.entity.QOrderItem.orderItem;


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
