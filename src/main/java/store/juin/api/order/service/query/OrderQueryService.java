package store.juin.api.order.service.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import store.juin.api.account.model.entity.Account;
import store.juin.api.common.exception.Msg;
import store.juin.api.common.handler.QueryTransactional;
import store.juin.api.order.model.entity.Order;
import store.juin.api.order.model.request.OrderRetrieveRequest;
import store.juin.api.order.model.response.OrderJoinResponse;
import store.juin.api.order.repository.jpa.OrderRepository;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderQueryService {
    private final QueryTransactional queryTransactional;

    private final OrderRepository orderRepository;

    public Page<OrderJoinResponse> readAll(Account account, OrderRetrieveRequest request, Pageable pageable) {
        return queryTransactional.execute(() ->
                orderRepository
                        .findOrderJoinOrderItemJoinItemJoinItemImageByAccountId(account.getId(), request, pageable)
                        .orElse(new PageImpl<>(new ArrayList<>(), pageable, 0))
        );
    }

    public List<Order> readAllByAccountId(Long accountId) {
        return queryTransactional.execute(() ->
                orderRepository.findAllByAccountId(accountId).orElse(new ArrayList<>())
        );
    }

    public Order readByIdAndAccountId(Long orderId, Long accountId) {
        return queryTransactional.execute(() ->
                orderRepository.findByIdAndAccountId(orderId, accountId)
                        .orElseThrow(() -> new EntityNotFoundException(Msg.ORDER_NOT_FOUND))
        );
    }
}
