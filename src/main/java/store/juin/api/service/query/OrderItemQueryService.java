package store.juin.api.service.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import store.juin.api.domain.entity.OrderItem;
import store.juin.api.exception.Msg;
import store.juin.api.handler.QueryTransactional;
import store.juin.api.repository.jpa.OrderItemRepository;

import javax.persistence.EntityNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderItemQueryService {
    private final QueryTransactional queryTransactional;

    private final OrderItemRepository orderItemRepository;

    public OrderItem readByOrderId(Long orderId) {
        return queryTransactional.execute(() ->
                orderItemRepository.findByOrderId(orderId)
                        .orElseThrow(() -> new EntityNotFoundException(Msg.ORDER_ITEM_NOT_FOUND))
        );
    }
}
