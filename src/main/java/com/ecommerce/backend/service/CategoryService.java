package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.Category;
import com.ecommerce.backend.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<Category> readAll() {
        return categoryRepository.findAll();
    }

    public Category readById(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(EntityNotFoundException::new);
    }
}
