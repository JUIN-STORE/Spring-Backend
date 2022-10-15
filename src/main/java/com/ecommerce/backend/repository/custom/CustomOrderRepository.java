package com.ecommerce.backend.repository.custom;

import com.ecommerce.backend.domain.response.OrderJoinResponse;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomOrderRepository {
    void deleteByAccountId(@Param("accountId") Long accountId);

    List<OrderJoinResponse>
        findOrderJoinOrderProductJoinProductByAccountId(@Param("accountId") Long accountId);
}
