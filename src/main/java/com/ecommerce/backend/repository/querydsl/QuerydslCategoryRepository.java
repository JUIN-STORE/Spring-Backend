package com.ecommerce.backend.repository.querydsl;

import com.ecommerce.backend.domain.entity.Category;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuerydslCategoryRepository {
    Optional<List<Category>> findAllByParentIsNull();
}
