package com.ecommerce.backend.domain.response;

import com.ecommerce.backend.domain.entity.Item;
import com.ecommerce.backend.domain.entity.ItemImage;
import com.ecommerce.backend.domain.enums.ItemStatus;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemResponse {
    @Data
    @Accessors(chain = true)
    public static class Read {
        private Long id;

        private String name;             // 제품의 이름

        private Integer price;                  // 제품의 가격

        private Integer quantity;               // 제품의 총 개수

        private Integer soldCount;              // 제품의 판매 개수

        private String description;             // 제품 설명

        @Enumerated(EnumType.STRING)
        private ItemStatus itemStatus;    // 제품의 상태

        private List<ItemImageResponse.Read> itemImageList = new ArrayList<>();

        // 단건
        public static Read from(Item item) {
            return new Read()
                    .setId(item.getId())
                    .setName(item.getName())
                    .setPrice(item.getPrice())
                    .setQuantity(item.getQuantity())
                    .setSoldCount(item.getSoldCount())
                    .setDescription(item.getDescription())
                    .setItemStatus(item.getItemStatus())
                    .setItemImageList(item.getItemImageList().stream() // 이 시점에 쿼리 나감
                            .map(ItemImageResponse.Read::of)
                            .collect(Collectors.toList()));
        }

        // 다건
        public static Read of(Item item, List<ItemImage> itemImageList) {
            Read read = new Read()
                    .setId(item.getId())
                    .setName(item.getName())
                    .setPrice(item.getPrice())
                    .setQuantity(item.getQuantity())
                    .setSoldCount(item.getSoldCount())
                    .setDescription(item.getDescription())
                    .setItemStatus(item.getItemStatus());

            if (itemImageList != null) {
                read.setItemImageList(itemImageList.stream()
                        .map(ItemImageResponse.Read::of)
                        .collect(Collectors.toList()));
            }

            return read;
        }
    }


    @Data
    @Accessors(chain = true)
    public static class Buy {
        private Long itemId;

        private String name;             // 제품의 이름

        private Integer price;                  // 제품의 가격

        private String description;             // 제품 설명
    }
}