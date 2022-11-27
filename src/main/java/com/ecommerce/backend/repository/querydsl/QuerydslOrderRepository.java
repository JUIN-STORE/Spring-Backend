package com.ecommerce.backend.repository.querydsl;

import com.ecommerce.backend.domain.request.OrderRequest;
import com.ecommerce.backend.domain.response.OrderJoinResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;


// FIXME: 아니 왜 ifs 패키지 안으로 넣으면 No converter found capable of converting from type 뜨지?
@Repository
public interface QuerydslOrderRepository {
    void deleteByAccountId(Long accountId);

    Optional<Page<OrderJoinResponse>> findOrderJoinOrderProductJoinProductByAccountId(Long accountId,
                                                                                      OrderRequest.Read request,
                                                                                      Pageable pageable);
}
