package com.ecommerce.backend.repository.querydsl;

import com.ecommerce.backend.domain.entity.ItemImage;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static com.ecommerce.backend.domain.entity.QItemImage.itemImage;

@RequiredArgsConstructor
public class QuerydslItemImageRepositoryImpl implements QuerydslItemImageRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<List<ItemImage>> findByThumbnail(boolean isThumbnail) {
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
