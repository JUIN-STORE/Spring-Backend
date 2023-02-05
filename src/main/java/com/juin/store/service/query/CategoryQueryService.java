package com.juin.store.service.query;

import com.juin.store.domain.entity.Category;
import com.juin.store.domain.response.CategoryResponse;
import com.juin.store.exception.Msg;
import com.juin.store.repository.jpa.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryQueryService {
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Category readById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException(Msg.CATEGORY_NOT_FOUND));
    }

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public List<Category> readAllByParentIdIsNull() {
        // 최상위 카테고리는 null, 최상위 카테고리만 구함.
        return categoryRepository.findAllByParentIsNull()
                .orElse(new ArrayList<>());
    }
}
