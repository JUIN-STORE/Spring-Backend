package store.juin.api.category.service.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import store.juin.api.cart.model.response.CategoryRetrieveResponse;
import store.juin.api.category.model.entity.Category;
import store.juin.api.category.repository.jpa.CategoryRepository;
import store.juin.api.common.exception.Msg;
import store.juin.api.common.handler.QueryTransactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryQueryService {
    private final QueryTransactional queryTransactional;

    private final CategoryRepository categoryRepository;

    public Category readById(Long categoryId) {
        return queryTransactional.execute(() ->
                categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new EntityNotFoundException(Msg.CATEGORY_NOT_FOUND))
        );
    }

    public List<CategoryRetrieveResponse> readAll() {
        final List<Category> categoryList = readAllByParentIdIsNull();
        final List<CategoryRetrieveResponse> response = new ArrayList<>();

        for (Category category : categoryList) {
            final List<CategoryRetrieveResponse.RetrieveChildList> childListResponse =
                    CategoryRetrieveResponse.RetrieveChildList.from(category.getChildList());
            response.add(CategoryRetrieveResponse.from(category, childListResponse));
        }

        return response;
    }

    public List<Category> readAllByParentIdIsNull() {
        return queryTransactional.execute(() ->
                // 최상위 카테고리는 null, 최상위 카테고리만 구함.
                categoryRepository.findAllByParentIsNull().orElse(new ArrayList<>())
        );
    }
}
