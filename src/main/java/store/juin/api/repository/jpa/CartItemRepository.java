package store.juin.api.repository.jpa;

import store.juin.api.domain.entity.CartItem;
import store.juin.api.repository.querydsl.QuerydslCartItemRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@EnableJpaRepositories
public interface CartItemRepository extends JpaRepository<CartItem, Long>, QuerydslCartItemRepository {
    CartItem findByCartIdAndItemId(Long cartId, Long itemId);

    // ref) https://extbrain.tistory.com/74
    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value = "DELETE cart_item " +
                    "FROM cart " +
                    "LEFT JOIN cart_item ON cart.cart_id = cart_item.cart_id " +
                    "WHERE cart.account_id=:accountId")
    int deleteByAccountId(@Param("accountId") Long accountId);
}
