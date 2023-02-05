package store.juin.api.repository.jpa;

import store.juin.api.domain.entity.Cart;
import store.juin.api.repository.querydsl.QuerydslCartRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long>, QuerydslCartRepository {
    Optional<Cart> findByAccountId(Long accountId);
}
