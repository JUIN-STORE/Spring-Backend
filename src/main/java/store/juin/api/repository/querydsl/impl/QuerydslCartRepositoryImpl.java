package store.juin.api.repository.querydsl.impl;

import store.juin.api.repository.querydsl.QuerydslCartRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import static store.juin.api.domain.entity.QCart.cart;


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
