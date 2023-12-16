package store.juin.api.itemimage.repository.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import store.juin.api.itemimage.model.entity.ItemImage;

import java.util.List;
import java.util.Optional;

import static store.juin.api.itemimage.model.entity.QItemImage.itemImage;

@RequiredArgsConstructor
public class QuerydslItemImageRepositoryImpl implements QuerydslItemImageRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<List<ItemImage>> findAllByThumbnail(boolean thumbnail) {
        return Optional.ofNullable(
                queryFactory.selectFrom(itemImage)
                        .where(itemImage.thumbnail.eq(thumbnail))
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
