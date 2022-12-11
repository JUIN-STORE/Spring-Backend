package com.ecommerce.backend.repository.querydsl;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuerydslOrderItemRepository {
    long deleteByOrderIdList(List<Long> orderIdList);
}
