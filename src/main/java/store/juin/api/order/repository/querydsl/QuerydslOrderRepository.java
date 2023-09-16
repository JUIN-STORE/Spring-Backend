package store.juin.api.order.repository.querydsl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import store.juin.api.order.model.request.OrderRequest;
import store.juin.api.order.model.response.OrderJoinResponse;

import java.util.Optional;


// FIXME: 아니 왜 ifs 패키지 안으로 넣으면 No converter found capable of converting from type 뜨지?
@Repository
public interface QuerydslOrderRepository {
    long deleteByAccountId(Long accountId);

    Long countOrderJoinOrderItemByAccountIdAndItemId(Long accountId, Long itemId);

    Optional<Page<OrderJoinResponse>> findOrderJoinOrderItemJoinItemJoinItemImageByAccountId(Long accountId,
                                                                                             OrderRequest.Retrieve request,
                                                                                             Pageable pageable);
}
