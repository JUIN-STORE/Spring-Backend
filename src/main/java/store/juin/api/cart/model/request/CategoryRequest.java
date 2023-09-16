package store.juin.api.cart.model.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import store.juin.api.category.model.entity.Category;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
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