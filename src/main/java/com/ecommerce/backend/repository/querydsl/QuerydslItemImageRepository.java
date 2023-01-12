package com.ecommerce.backend.repository.querydsl;

import com.ecommerce.backend.domain.entity.ItemImage;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuerydslItemImageRepository {
    Optional<List<ItemImage>> findAllByThumbnail(boolean isThumbnail);

    Optional<List<ItemImage>> findAllByItemIdIn(List<Long> itemIdList);
}
