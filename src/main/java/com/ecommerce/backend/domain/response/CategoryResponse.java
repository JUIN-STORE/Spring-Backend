package com.ecommerce.backend.domain.response;

import com.ecommerce.backend.domain.entity.Category;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

public class CategoryResponse {
    @Data
    @Accessors(chain = true)
    public static class Read {
        private Long id;

        private String categoryName;

        private Long depth;

        private List<CategoryResponse.ReadChildList> childList = new ArrayList<>();

        public static Read from(Category category, List<CategoryResponse.ReadChildList> childList) {
            return new Read()
                    .setId(category.getId())
                    .setCategoryName(category.getCategoryName())
                    .setDepth(category.getDepth())
                    .setChildList(childList);
        }
    }

    @Data @Accessors(chain = true)
    public static class ReadChildList {
        private Long id;

        private String categoryName;

        private Long depth;

        private List<CategoryResponse.ReadChildList> childList = new ArrayList<>();

        public static List<ReadChildList> from(List<Category> categoryChildList) {
            List<ReadChildList> response = new ArrayList<>();

            for (Category category : categoryChildList) {
                response.add(
                        new ReadChildList()
                                .setId(category.getId())
                                .setCategoryName(category.getCategoryName())
                                .setDepth(category.getDepth())
                                .setChildList(from(category.getChildList()))
                );
            }

            return response;
        }
    }
}
