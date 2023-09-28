package store.juin.api.cartitem.model.request;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class CartItemBuyRequest {
    private List<Long> itemIdList;
}