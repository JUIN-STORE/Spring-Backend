package store.juin.api.orderitem.service.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import store.juin.api.orderitem.model.entity.OrderItem;
import store.juin.api.orderitem.repository.jpa.OrderItemRepository;

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
