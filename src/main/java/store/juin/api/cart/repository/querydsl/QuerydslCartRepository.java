package store.juin.api.cart.repository.querydsl;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuerydslCartRepository {
    long deleteByAccountId(Long accountId);

    Optional<Long> countItemsByAccountId(Long accountId);
}
