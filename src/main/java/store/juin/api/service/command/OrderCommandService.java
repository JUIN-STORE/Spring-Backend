package store.juin.api.service.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.juin.api.domain.entity.*;
import store.juin.api.domain.request.OrderRequest;
import store.juin.api.domain.response.OrderResponse;
import store.juin.api.exception.Msg;
import store.juin.api.repository.jpa.OrderRepository;
import store.juin.api.service.query.AddressQueryService;
import store.juin.api.service.query.ItemQueryService;
import store.juin.api.service.query.OrderQueryService;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderCommandService {
    private final OrderRepository orderRepository;

    private final ItemQueryService itemQueryService;
    private final OrderQueryService orderQueryService;
    private final AddressQueryService addressQueryService;

    private final AddressCommandService addressCommandService;
    private final CartItemCommandService cartItemCommandService;
    private final DeliveryCommandService deliveryCommandService;
    private final OrderItemCommandService orderItemCommandService;

    @Transactional
    public Long add(Account account, OrderRequest.Create request) {
        // 엔티티 조회
        Address deliveryAddress;

        if (request.getDeliveryAddress() == null) {
            throw new InvalidParameterException(Msg.ORDER_DELIVERY_ADDRESS_NOT_FOUND);
        }
        if (request.getDeliveryAddress().isDefaultAddress()) {
            deliveryAddress = addressQueryService.readByAccountIdAndDefaultAddress(account.getId());
        } else {
            deliveryAddress = addressCommandService.addIfNull(account, request.getDeliveryAddress());
        }

        if (request.getDeliveryReceiver() == null) {
            throw new InvalidParameterException(Msg.ORDER_DELIVERY_RECEIVER_REQUIRED);
        }
        final DeliveryReceiver deliveryReceiver = request.getDeliveryReceiver().toDeliveryReceiver();

        // 배송 정보 생성
        final Delivery delivery = Delivery.createDelivery(deliveryReceiver, deliveryAddress);
        deliveryCommandService.add(delivery); // update item 쿼리 날아감. (오류)

        final List<Long> itemIdList = request.getItemIdList();
        List<Item> itemList = itemQueryService.readAllByIdList(itemIdList);

        // 주문 상품 생성
        final Order order = Order.createOrder(account, delivery);
        orderRepository.save(order);

        for (Item item : itemList) {
            final OrderItem orderItem
                    = OrderItem.createOrderItem(item, request.getCount(), item.getPrice() * request.getCount());

            order.addOrderItem(orderItem);
            orderItemCommandService.add(orderItem);
        }
        cartItemCommandService.removeByItemIdList(itemIdList);

        return order.getId();
    }

    // 주문 취소
    @Transactional
    public Long cancel(Long orderId, Long accountId) {
        // 주문 엔티티 조회
        final Order order = orderQueryService.readByIdAndAccountId(orderId, accountId);
        order.cancel();

        return order.getId();
    }

    public long removeByAccountId(Long accountId) {
        return orderRepository.deleteByAccountId(accountId);
    }

    public long countByAccountIdAndItemId(Long accountId, Long item) {
        return orderRepository.countOrderJoinOrderItemByAccountIdAndItemId(accountId, item);
    }

    @Transactional
    public OrderResponse.Delete remove(Long accountId) {
        final List<Order> orderList = orderQueryService.readAllByAccountId(accountId);
        final List<Long> orderIdList = orderList.stream().map(Order::getId).collect(Collectors.toList());
        long orderItemDeleteCount = orderItemCommandService.removeByOrderIdList(orderIdList);
        long ordersDeleteCount = removeByAccountId(accountId);

        return OrderResponse.Delete.of(ordersDeleteCount, orderItemDeleteCount);
    }
}
