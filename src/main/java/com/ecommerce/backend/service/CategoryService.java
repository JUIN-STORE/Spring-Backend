package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.Category;
import com.ecommerce.backend.domain.request.CategoryRequest;
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
public class CategoryService {
    private final CategoryRepository categoryRepository;

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

    @Transactional
    public Long add(CategoryRequest.Create request) {
        final Long parentId = request.getParentId();

        Category parent = null;

        if (parentId > 0) {
            // 0보다 크면 하위 카테고리, 아니면 최상위 카테고리
            parent = this.readById(parentId);
        }

        final Category category = request.toCategory(parent);
        final Category save = categoryRepository.save(category);

        return save.getId();
    }

    // FIXME: FK 걸려있기 때문에 modify, remove 어떻게 구현할지 고민하기

    private List<Category> readAllByParentIdIsNull() {
        // 최상위 카테고리는 null, 최상위 카테고리만 구함.
        return categoryRepository.findAllByParentIsNull()
                .orElse(new ArrayList<>());
    }
}
