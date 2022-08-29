package com.ecommerce.backend.domain.response;

import com.ecommerce.backend.domain.entity.Category;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryResponse {
    @Data
    @Accessors(chain = true)
    public static class Read {
        private Long id;

        private String categoryName;

        private Long depth;

        private Category parent;

        private List<CategoryResponse.Read> childList = new ArrayList<>();

        public static Read from(Category category) {
            return new Read()
                    .setId(category.getId())
                    .setCategoryName(category.getCategoryName())
                    .setDepth(category.getDepth())
                    .setChildList(category.getChildList().stream().map(Read::from).collect(Collectors.toList()));
        }
    }
}
