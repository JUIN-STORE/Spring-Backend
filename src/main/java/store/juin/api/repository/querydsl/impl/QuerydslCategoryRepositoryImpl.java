package store.juin.api.repository.querydsl.impl;

import store.juin.api.domain.entity.Category;
import store.juin.api.repository.querydsl.QuerydslCategoryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static store.juin.api.domain.entity.QCategory.category;


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
