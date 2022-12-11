package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.OrderProduct;
import com.ecommerce.backend.exception.Msg;
import com.ecommerce.backend.repository.jpa.OrderProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderProductService {
    private final OrderProductRepository orderProductRepository;

    public int removeByOrderIdList(List<Long> orderIdList) {
        return orderProductRepository.deleteByOrderIdList(orderIdList);
    }

    public void add(OrderProduct orderProduct) {
        orderProductRepository.save(orderProduct);
    }

    public OrderProduct readByOrderId(Long orderId) {
        return orderProductRepository.findByOrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException(Msg.ORDER_PRODUCT_NOT_FOUND));
    }
}
