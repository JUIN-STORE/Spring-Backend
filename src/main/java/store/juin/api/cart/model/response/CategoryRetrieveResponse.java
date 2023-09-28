package store.juin.api.cart.model.response;

import lombok.Data;
import lombok.experimental.Accessors;
import store.juin.api.category.model.entity.Category;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class CategoryRetrieveResponse {
    private Long id;

    private String categoryName;

    private Long depth;

    private List<RetrieveChildList> childList = new ArrayList<>();

    public static CategoryRetrieveResponse from(Category category, List<RetrieveChildList> childList) {
        return new CategoryRetrieveResponse()
                .setId(category.getId())
                .setCategoryName(category.getCategoryName())
                .setDepth(category.getDepth())
                .setChildList(childList);
    }


    @Data
    @Accessors(chain = true)
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
