package store.juin.api.order.model.request;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;
import store.juin.api.order.enumeration.OrderStatus;

import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class OrderRetrieveRequest {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private OrderStatus orderStatus;
}