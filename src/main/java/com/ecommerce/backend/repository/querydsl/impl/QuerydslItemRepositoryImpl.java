package com.ecommerce.backend.repository.querydsl.impl;

import com.ecommerce.backend.domain.entity.Item;
import com.ecommerce.backend.repository.querydsl.QuerydslItemRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static com.ecommerce.backend.domain.entity.QItem.item;


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
}