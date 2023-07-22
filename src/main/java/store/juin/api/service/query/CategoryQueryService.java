package store.juin.api.service.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import store.juin.api.domain.entity.Category;
import store.juin.api.domain.response.CategoryResponse;
import store.juin.api.exception.Msg;
import store.juin.api.handler.QueryTransactional;
import store.juin.api.repository.jpa.CategoryRepository;

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

    public List<CategoryResponse.Retrieve> readAll() {
        final List<Category> categoryList = readAllByParentIdIsNull();
        final List<CategoryResponse.Retrieve> response = new ArrayList<>();

        for (Category category : categoryList) {
            final List<CategoryResponse.RetrieveChildList> childListResponse =
                    CategoryResponse.RetrieveChildList.from(category.getChildList());
            response.add(CategoryResponse.Retrieve.from(category, childListResponse));
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
