package store.juin.api.service.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.juin.api.domain.entity.Account;
import store.juin.api.domain.entity.Order;
import store.juin.api.domain.request.OrderRequest;
import store.juin.api.domain.response.OrderJoinResponse;
import store.juin.api.exception.Msg;
import store.juin.api.repository.jpa.OrderRepository;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderQueryService {
    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public Page<OrderJoinResponse> readAll(Account account, OrderRequest.Retrieve request, Pageable pageable) {
        return orderRepository
                .findOrderJoinOrderItemJoinItemByAccountId(account.getId(), request, pageable)
                .orElse(new PageImpl<>(new ArrayList<>(), pageable, 0));
    }

    @Transactional(readOnly = true)
    public List<Order> readAllByAccountId(Long accountId) {
        return orderRepository
                .findAllByAccountId(accountId)
                .orElse(new ArrayList<>());
    }

    @Transactional(readOnly = true)
    public Order readByIdAndAccountId(Long orderId, Long accountId) {
        return orderRepository
                .findByIdAndAccountId(orderId, accountId)
                .orElseThrow(() -> new EntityNotFoundException(Msg.ORDER_NOT_FOUND));
    }
}
