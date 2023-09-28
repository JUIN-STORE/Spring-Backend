package store.juin.api.order.model.request;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class OrderCancelRequest {
    private Long orderId;
}