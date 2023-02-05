package com.juin.store.repository.querydsl.impl;

import com.juin.store.repository.querydsl.QuerydslCartRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import static com.juin.store.domain.entity.QCart.cart;

@RequiredArgsConstructor
public class QuerydslCartRepositoryImpl implements QuerydslCartRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    @Transactional
    public long deleteByAccountId(Long accountId) {
        return queryFactory
                .delete(cart)
                .where(cart.account.id.eq(accountId))
                .execute();
    }
}
