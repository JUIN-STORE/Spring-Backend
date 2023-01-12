package com.ecommerce.backend.service.command;

import com.ecommerce.backend.domain.entity.OrderItem;
import com.ecommerce.backend.repository.jpa.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderItemCommandService {
    private final OrderItemRepository orderItemRepository;

    public long removeByOrderIdList(List<Long> orderIdList) {
        return orderItemRepository.deleteByOrderIdList(orderIdList);
    }

    public void add(OrderItem orderItem) {
        orderItemRepository.save(orderItem);
    }
}
