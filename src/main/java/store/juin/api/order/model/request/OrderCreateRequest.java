package store.juin.api.order.model.request;

import lombok.Data;
import lombok.experimental.Accessors;
import store.juin.api.address.model.request.AddressCreateRequest;
import store.juin.api.delivery.model.request.DeliveryReceiverRequest;
import store.juin.api.order.enumeration.OrderStatus;
import store.juin.api.order.model.entity.Order;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Accessors(chain = true)
public class OrderCreateRequest {
    private List<Long> itemIdList;

    private int count;

    private int grandTotal;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus; // 주문 상태

    private DeliveryReceiverRequest deliveryReceiver;

    private AddressCreateRequest deliveryAddress;

    public Order toOrder() {
        return Order.builder()
                .orderStatus(this.orderStatus)
                .orderDate(LocalDateTime.now())
                .build();
    }
}
