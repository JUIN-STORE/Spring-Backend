package store.juin.api.order.model.response;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class OrderDeleteResponse {
    private long ordersDeletedCount;

    private long orderItemDeletedCount;

    public static OrderDeleteResponse of(long ordersDeleteCount, long orderItemDeleteCount) {
        return new OrderDeleteResponse()
                .setOrdersDeletedCount(ordersDeleteCount)
                .setOrderItemDeletedCount(orderItemDeleteCount);
    }
}