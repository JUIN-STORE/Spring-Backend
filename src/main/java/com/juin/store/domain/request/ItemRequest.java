package com.juin.store.domain.request;

import com.juin.store.domain.entity.Category;
import com.juin.store.domain.entity.Item;
import com.juin.store.domain.enums.ItemStatus;
import lombok.Data;
import lombok.experimental.Accessors;

public class ItemRequest {
    @Data @Accessors(chain = true)
    public static class Create {
        private Long categoryId;

        private String name;

        private Integer price;          // 제품의 가격

        private Integer quantity;       // 제품의 총 개수

        private String description;

        public Item toItem(Category category) {
            return Item.builder()
                    .category(category)
                    .name(this.name)
                    .price(this.price)
                    .quantity(this.quantity)
                    .description(this.description)
                    .itemStatus(ItemStatus.READY)
                    .soldCount(0)
                    .build();
        }
    }
}