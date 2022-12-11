package com.ecommerce.backend.repository.querydsl;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuerydslOrderProductRepository {
    long deleteByOrderIdList(List<Long> orderIdList);
}
