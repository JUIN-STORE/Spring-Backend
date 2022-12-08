package com.ecommerce.backend.repository.querydsl;

import org.springframework.stereotype.Repository;

@Repository
public interface QuerydslCartRepository {
    long deleteByAccountId(Long accountId);
}
