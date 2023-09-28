package store.juin.api.cart.model.request;

import lombok.Data;
import lombok.experimental.Accessors;
import store.juin.api.category.model.entity.Category;

@Data
@Accessors(chain = true)
public class CategoryCreateRequest {
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