package store.juin.api.repository.querydsl.impl;

import store.juin.api.domain.enums.OrderStatus;
import store.juin.api.domain.request.OrderRequest;
import store.juin.api.domain.response.OrderJoinResponse;
import store.juin.api.repository.querydsl.QuerydslOrderRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static store.juin.api.domain.entity.QItem.item;
import static store.juin.api.domain.entity.QOrder.order;
import static store.juin.api.domain.entity.QOrderItem.orderItem;


@RequiredArgsConstructor
public class QuerydslOrderRepositoryImpl implements QuerydslOrderRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public long deleteByAccountId(Long accountId) {
        return queryFactory
                .delete(order)
                .where(order.account.id.eq(accountId))
                .execute();
    }

    @Override
    public Optional<Page<OrderJoinResponse>> findOrderJoinOrderItemJoinItemByAccountId(Long accountId,
                                                                                       OrderRequest.Retrieve request,
                                                                                       Pageable pageable) {
        // FIXME: 더 좋은 방법 있으면 변경하기
        List<OrderJoinResponse> orderJoinResponseList = queryFactory
                .select(Projections.fields(OrderJoinResponse.class
                                , order.orderDate
                                , orderItem.orderCount
                                , item.id.as("itemId")
                                , item.price
                                , item.name
                                , order.id.as("ordersId")
                                , orderItem.item.id.as("orderItemId")
                                , order.delivery.id.as("deliveryId")
                                , order.orderStatus
                        )
                )
                .from(order)
                .join(orderItem)
                .on(order.id.eq(orderItem.order.id))
                .join(item)
                .on(item.id.eq(orderItem.item.id))
                .where(order.account.id.eq(accountId),
                        orderDateBetween(
                                request.getStartDate().atStartOfDay(),
                                request.getEndDate().atStartOfDay()
                        ),
                        orderStatusEq(request.getOrderStatus()))
                .orderBy(order.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return Optional.of(new PageImpl<>(orderJoinResponseList, pageable, countOrderItem(accountId, request)));
    }

    private Long countOrderItem(Long accountId, OrderRequest.Retrieve request) {
        return queryFactory.select(Wildcard.count)
                .from(order)
                .join(orderItem)
                .on(order.id.eq(orderItem.order.id))
                .where(order.account.id.eq(accountId),
                        orderDateBetween(
                                request.getStartDate().atStartOfDay(),
                                request.getEndDate().atStartOfDay()
                        ),
                        orderStatusEq(request.getOrderStatus()))
                .fetchOne();
    }

    private BooleanExpression orderDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return order.orderDate.between(startDate, endDate.plusDays(1));
    }

    private BooleanExpression orderStatusEq(OrderStatus orderStatus) {
        return orderStatus != null ? order.orderStatus.eq(orderStatus) : null;
    }
}