package store.juin.api.repository.querydsl.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;
import store.juin.api.domain.entity.Item;
import store.juin.api.repository.querydsl.QuerydslItemRepository;

import java.util.List;
import java.util.Optional;

import static com.querydsl.core.types.dsl.Wildcard.count;
import static store.juin.api.domain.entity.QItem.item;


@RequiredArgsConstructor
public class QuerydslItemRepositoryImpl implements QuerydslItemRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<List<Item>> findAllByIdIn(List<Long> itemIdList) {
        return Optional.ofNullable(
                queryFactory
                        .select(item)
                        .from(item)
                        .where(item.id.in(itemIdList))
                        .fetch()
        );
    }

    @Override
    public Optional<Page<Item>> findByNameContainingAndCategoryId(Pageable pageable,
                                                                  String name,
                                                                  Long categoryId) {
        final List<Item> itemList =
                queryFactory
                        .select(item)
                        .from(item)
                        .where(orderNameContains(name), categoryIdEq(categoryId))
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch();

        return Optional.of(new PageImpl<>(itemList, pageable, countItem(name, categoryId)));
    }

    private Long countItem(String name, Long categoryId) {
        return queryFactory.select(count)
                .from(item)
                .where(orderNameContains(name), categoryIdEq(categoryId))
                .fetchOne();
    }

    private BooleanExpression orderNameContains(String name) {
        return StringUtils.hasText(name) ? item.name.contains(name) : null;
    }

    private BooleanExpression categoryIdEq(Long categoryId) {
        return categoryId != null ? item.category.id.eq(categoryId) : null;
    }
}