package store.juin.api.cart.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import store.juin.api.cart.model.entity.Cart;
import store.juin.api.cart.repository.querydsl.QuerydslCartRepository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long>, QuerydslCartRepository {
    Optional<Cart> findByAccountId(Long accountId);
}
