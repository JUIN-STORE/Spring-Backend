package com.ecommerce.backend.repository.querydsl.impl;

import com.ecommerce.backend.domain.entity.Category;
import com.ecommerce.backend.repository.querydsl.QuerydslCategoryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static com.ecommerce.backend.domain.entity.QCategory.category;

@RequiredArgsConstructor
public class QuerydslCategoryRepositoryImpl implements QuerydslCategoryRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<List<Category>> findAllByParentIsNull() {
        return Optional.ofNullable(
                queryFactory
                        .select(category)
                        .from(category)
                        .where(category.parent.isNull())
                        .fetch()
        );
    }
}
