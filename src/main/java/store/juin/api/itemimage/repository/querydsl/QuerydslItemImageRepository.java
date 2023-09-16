package store.juin.api.itemimage.repository.querydsl;

import org.springframework.stereotype.Repository;
import store.juin.api.itemimage.model.entity.ItemImage;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuerydslItemImageRepository {
    Optional<List<ItemImage>> findAllByThumbnail(boolean thumbnail);

    Optional<List<ItemImage>> findAllByItemIdIn(List<Long> itemIdList);
}
