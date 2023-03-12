package store.juin.api.repository.querydsl.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import store.juin.api.repository.querydsl.QuerydslCartRepository;

import java.util.Optional;

import static store.juin.api.domain.entity.QCart.cart;
import static store.juin.api.domain.entity.QCartItem.cartItem;


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

    @Override
    public Optional<Long> countItemsByAccountId(Long accountId) {
        return Optional.ofNullable(queryFactory
                .select(cart.count())
                .from(cart)
                .join(cartItem).on(cartItem.cart.id.eq(cart.id))
                .where(cart.account.id.eq(accountId))
                .fetchOne());
    }
}
