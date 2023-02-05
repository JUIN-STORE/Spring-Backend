package com.juin.store.repository.querydsl;

import com.juin.store.domain.entity.Item;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuerydslItemRepository {
    Optional<List<Item>> findAllByIdIn(List<Long> itemIdList);
}
