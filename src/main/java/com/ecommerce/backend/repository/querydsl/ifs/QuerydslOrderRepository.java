package com.ecommerce.backend.repository.querydsl.ifs;

import com.ecommerce.backend.domain.response.OrderJoinResponse;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuerydslOrderRepository {
    void deleteByAccountId(Long accountId);

    List<OrderJoinResponse>
        findOrderJoinOrderProductJoinProductByAccountId(Long accountId);
}
