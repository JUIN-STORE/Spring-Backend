package com.juin.store.service.query;

import com.juin.store.domain.entity.OrderItem;
import com.juin.store.exception.Msg;
import com.juin.store.repository.jpa.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderItemQueryService {
    private final OrderItemRepository orderItemRepository;

    @Transactional(readOnly = true)
    public OrderItem readByOrderId(Long orderId) {
        return orderItemRepository.findByOrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException(Msg.ORDER_ITEM_NOT_FOUND));
    }
}
