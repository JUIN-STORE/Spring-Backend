package store.juin.api.item.model.response;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ItemBuyResponse {
    private Long itemId;

    private String itemName;             // 제품의 이름

    private Integer price;                  // 제품의 가격

    private String description;             // 제품 설명
}