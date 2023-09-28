package store.juin.api.item.model.response;

import lombok.Data;
import lombok.experimental.Accessors;
import store.juin.api.item.enumeration.ItemStatus;
import store.juin.api.item.model.entity.Item;
import store.juin.api.itemcategory.model.response.ItemImageRetrieveResponse;
import store.juin.api.itemimage.model.entity.ItemImage;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Accessors(chain = true)
public class ItemRetrieveResponse {
    private Long id;

    private String name;             // 제품의 이름

    private Integer price;                  // 제품의 가격

    private Integer quantity;               // 제품의 총 개수

    private Integer soldCount;              // 제품의 판매 개수

    private String description;             // 제품 설명

    @Enumerated(EnumType.STRING)
    private ItemStatus itemStatus;    // 제품의 상태

    private List<ItemImageRetrieveResponse> itemImageList = new ArrayList<>();

    // 단건
    public static ItemRetrieveResponse from(Item item) {
        return new ItemRetrieveResponse()
                .setId(item.getId())
                .setName(item.getName())
                .setPrice(item.getPrice())
                .setQuantity(item.getQuantity())
                .setSoldCount(item.getSoldCount())
                .setDescription(item.getDescription())
                .setItemStatus(item.getItemStatus())
                .setItemImageList(item.getItemImageList().stream() // 이 시점에 쿼리 나감
                        .map(ItemImageRetrieveResponse::of)
                        .collect(Collectors.toList()));
    }

    // 다건
    public static ItemRetrieveResponse of(Item item, List<ItemImage> itemImageList) {
        ItemRetrieveResponse itemRetrieveResponse = new ItemRetrieveResponse()
                .setId(item.getId())
                .setName(item.getName())
                .setPrice(item.getPrice())
                .setQuantity(item.getQuantity())
                .setSoldCount(item.getSoldCount())
                .setDescription(item.getDescription())
                .setItemStatus(item.getItemStatus());

        if (itemImageList != null) {
            itemRetrieveResponse.setItemImageList(itemImageList.stream()
                    .map(ItemImageRetrieveResponse::of)
                    .collect(Collectors.toList()));
        }

        return itemRetrieveResponse;
    }
}
