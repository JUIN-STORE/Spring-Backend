package com.ecommerce.backend.domain.request;

import com.ecommerce.backend.domain.entity.Category;
import lombok.Data;
import lombok.experimental.Accessors;

public class CategoryRequest {
    @Data @Accessors(chain = true)
    public static class Create {
        private String categoryName;

        private Long parentId;

        public Category toCategory(Category parent) {
            return Category.builder()
                    .categoryName(this.categoryName)
                    .depth(parent == null ? 1 : parent.getDepth() + 1)
                    .parent(parent)
                    .build();
        }
    }
}