package com.juin.store.repository.querydsl;

import com.juin.store.domain.entity.Category;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuerydslCategoryRepository {
    Optional<List<Category>> findAllByParentIsNull();
}
