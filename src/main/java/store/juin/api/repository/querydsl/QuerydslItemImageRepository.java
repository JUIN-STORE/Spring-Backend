package store.juin.api.repository.querydsl;

import store.juin.api.domain.entity.ItemImage;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuerydslItemImageRepository {
    Optional<List<ItemImage>> findAllByThumbnail(boolean thumbnail);

    Optional<List<ItemImage>> findAllByItemIdIn(List<Long> itemIdList);
}
