package store.juin.api.category.repository.querydsl.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import store.juin.api.category.model.entity.Category;
import store.juin.api.category.repository.querydsl.QuerydslCategoryRepository;

import java.util.List;
import java.util.Optional;

import static store.juin.api.category.model.entity.QCategory.category;


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
