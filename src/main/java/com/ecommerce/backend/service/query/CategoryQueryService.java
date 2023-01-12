package com.ecommerce.backend.service.query;

import com.ecommerce.backend.domain.entity.Category;
import com.ecommerce.backend.domain.response.CategoryResponse;
import com.ecommerce.backend.exception.Msg;
import com.ecommerce.backend.repository.jpa.CategoryRepository;
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
    public List<CategoryResponse.Read> readAll() {
        final List<Category> categoryList = readAllByParentIdIsNull();
        final List<CategoryResponse.Read> response = new ArrayList<>();

        for (Category category : categoryList) {
            final List<CategoryResponse.ReadChildList> childListResponse =
                    CategoryResponse.ReadChildList.from(category.getChildList());
            response.add(CategoryResponse.Read.from(category, childListResponse));
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
