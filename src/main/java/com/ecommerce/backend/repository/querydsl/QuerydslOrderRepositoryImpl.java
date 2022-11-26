package com.ecommerce.backend.repository.querydsl;

import com.ecommerce.backend.domain.enums.OrderStatus;
import com.ecommerce.backend.domain.response.OrderJoinResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.ecommerce.backend.domain.entity.QOrder.order;
import static com.ecommerce.backend.domain.entity.QOrderProduct.orderProduct;
import static com.ecommerce.backend.domain.entity.QProduct.product;


@RequiredArgsConstructor
public class QuerydslOrderRepositoryImpl implements QuerydslOrderRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public void deleteByAccountId(Long accountId) {
        queryFactory
                .delete(order)
                .where(order.account.id.eq(accountId))
                .execute();
    }

    @Override
    public Optional<List<OrderJoinResponse>> findOrderJoinOrderProductJoinProductByAccountId(Long accountId,
                                                                                             LocalDateTime startDate,
                                                                                             LocalDateTime endDate,
                                                                                             OrderStatus orderStatus) {
        return Optional.ofNullable(
                queryFactory
                    .select(Projections.fields(OrderJoinResponse.class
                        , order.orderDate
                        , orderProduct.orderCount
                        , product.id.as("productId")
                        , product.price
                        , product.productName
                        , order.id.as("ordersId")
                        , orderProduct.product.id.as("orderProductId")
                        , order.delivery.id.as("deliveryId")
                        , order.orderStatus
                           )
                        )
                    .from(order)
                    .join(orderProduct)
                    .on(order.id.eq(orderProduct.order.id))
                    .join(product)
                    .on(product.id.eq(orderProduct.product.id))
                    .where(order.account.id.eq(accountId), orderDateBetween(startDate, endDate), orderStatusEq(orderStatus))
                        .orderBy(order.id.desc())
                    .fetch()
        );
    }

    private BooleanExpression orderDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return order.orderDate.between(startDate, endDate.plusDays(1));
    }

    private BooleanExpression orderStatusEq(OrderStatus orderStatus) {
        return orderStatus != null ? order.orderStatus.eq(orderStatus) : null;
    }
}