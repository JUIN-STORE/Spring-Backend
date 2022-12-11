package com.ecommerce.backend.repository.querydsl;

import com.ecommerce.backend.domain.entity.Item;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuerydslItemRepository {
    Optional<List<Item>> findAllByIdIn(List<Long> itemIdList);
}
