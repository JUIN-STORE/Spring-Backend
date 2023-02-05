package com.juin.store.repository.querydsl.impl;

import com.juin.store.domain.entity.ItemImage;
import com.juin.store.repository.querydsl.QuerydslItemImageRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static com.juin.store.domain.entity.QItemImage.itemImage;

@RequiredArgsConstructor
public class QuerydslItemImageRepositoryImpl implements QuerydslItemImageRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<List<ItemImage>> findAllByThumbnail(boolean isThumbnail) {
        return Optional.ofNullable(
                queryFactory.selectFrom(itemImage)
                        .where(itemImage.thumbnail.eq(isThumbnail))
                        .fetch()
        );
    }

    @Override
    public Optional<List<ItemImage>> findAllByItemIdIn(List<Long> itemIdList) {
        return Optional.ofNullable(
                queryFactory
                        .select(itemImage)
                        .from(itemImage)
                        .where(itemImage.item.id.in(itemIdList))
                        .fetch()
        );
    }
}
