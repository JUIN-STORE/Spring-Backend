package com.ecommerce.backend.repository.querydsl;

import com.ecommerce.backend.domain.enums.OrderStatus;
import com.ecommerce.backend.domain.response.OrderJoinResponse;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


// FIXME: 아니 왜 ifs 패키지 안으로 넣으면 No converter found capable of converting from type 뜨지?
@Repository
public interface QuerydslOrderRepository {
    void deleteByAccountId(Long accountId);

    Optional<List<OrderJoinResponse>> findOrderJoinOrderProductJoinProductByAccountId(Long accountId,
                                                                                      LocalDateTime startDate,
                                                                                      LocalDateTime endDate,
                                                                                      OrderStatus orderStatus);
}
