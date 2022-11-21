package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.Category;
import com.ecommerce.backend.domain.request.CategoryRequest;
import com.ecommerce.backend.repository.jpa.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<Category> readByParentId() {
        // 최상위 카테고리는 null
        return categoryRepository.findByParentId(null);
    }

    public Category readById(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public Long add(CategoryRequest.Create request) {
        final Long parentId = request.getParentId();

        Category parent = null;

        // 0일 때 최상위에 추가함.
        if (parentId > 0) parent = this.readById(parentId);

        final Category category = request.toCategory(parent);
        final Category save = categoryRepository.save(category);

        return save.getId();
    }
}
