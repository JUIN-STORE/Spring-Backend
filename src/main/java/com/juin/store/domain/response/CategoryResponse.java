package com.juin.store.domain.response;

import com.juin.store.domain.entity.Category;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

public class CategoryResponse {
    @Data @Accessors(chain = true)
    public static class Retrieve {
        private Long id;

        private String categoryName;

        private Long depth;

        private List<RetrieveChildList> childList = new ArrayList<>();

        public static Retrieve from(Category category, List<RetrieveChildList> childList) {
            return new Retrieve()
                    .setId(category.getId())
                    .setCategoryName(category.getCategoryName())
                    .setDepth(category.getDepth())
                    .setChildList(childList);
        }
    }

    @Data @Accessors(chain = true)
    public static class RetrieveChildList {
        private Long id;

        private String categoryName;

        private Long depth;

        private List<RetrieveChildList> childList = new ArrayList<>();

        public static List<RetrieveChildList> from(List<Category> categoryChildList) {
            List<RetrieveChildList> response = new ArrayList<>();

            for (Category category : categoryChildList) {
                response.add(
                        new RetrieveChildList()
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
