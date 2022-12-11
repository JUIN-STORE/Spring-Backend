package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.OrderItem;
import com.ecommerce.backend.exception.Msg;
import com.ecommerce.backend.repository.jpa.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderItemService {
    private final OrderItemRepository orderItemRepository;

    public long removeByOrderIdList(List<Long> orderIdList) {
        return orderItemRepository.deleteByOrderIdList(orderIdList);
    }

    public void add(OrderItem orderItem) {
        orderItemRepository.save(orderItem);
    }

    public OrderItem readByOrderId(Long orderId) {
        return orderItemRepository.findByOrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException(Msg.ORDER_PRODUCT_NOT_FOUND));
    }
}
