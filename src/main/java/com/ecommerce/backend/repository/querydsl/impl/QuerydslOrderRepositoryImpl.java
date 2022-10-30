package com.ecommerce.backend.repository.querydsl.impl;

import com.ecommerce.backend.domain.response.OrderJoinResponse;
import com.ecommerce.backend.repository.querydsl.ifs.QuerydslOrderRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

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
    public List<OrderJoinResponse> findOrderJoinOrderProductJoinProductByAccountId(Long accountId) {
        return queryFactory
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
                ))
                .from(order)
                .join(orderProduct)
                .on(order.id.eq(orderProduct.order.id))
                .join(product)
                .on(product.id.eq(orderProduct.product.id))
                .where(order.account.id.eq(accountId))
                .fetch();
    }
}