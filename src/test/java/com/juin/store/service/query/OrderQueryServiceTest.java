package com.juin.store.service.query;

import com.juin.store.domain.entity.Account;
import com.juin.store.domain.entity.Order;
import com.juin.store.domain.request.OrderRequest;
import com.juin.store.domain.response.OrderJoinResponse;
import com.juin.store.repository.jpa.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrderQueryServiceTest {
    @InjectMocks
    private OrderQueryService sut;

    @Mock
    private OrderRepository mockOrderRepository;

    @Nested
    @DisplayName("주문 상세 조회")
    class RetrieveTest {
        @Test
        @DisplayName("성공")
        void readTest01() {
            // given
            var page = 0;
            var size = 10;
            var account = makeAccount();
            var request = new OrderRequest.Retrieve();
            var pageRequest = PageRequest.of(page, size);

            var orderJoinResponseList = new ArrayList<OrderJoinResponse>();
            orderJoinResponseList.add(new OrderJoinResponse());
            var responsePage = new PageImpl<>(orderJoinResponseList);

            given(mockOrderRepository.findOrderJoinOrderItemJoinItemByAccountId(account.getId(), request, pageRequest))
                    .willReturn(Optional.of(responsePage));

            // when
            Page<OrderJoinResponse> actual = sut.readAll(account, request, pageRequest);

            // then
            assertEquals(orderJoinResponseList, actual.getContent());
        }
    }

    @Nested
    @DisplayName("회원 주문 내역 조회")
    class RetrieveByAccountIdTest {
        @Test
        @DisplayName("성공")
        void readByAccountIdTest() {
            // given
            var accountId = 1L;
            var orderList = List.of(makeOrder());
            given(mockOrderRepository.findAllByAccountId(accountId)).willReturn(Optional.of(orderList));

            // when
            List<Order> actual = sut.readAllByAccountId(accountId);

            // then
            assertEquals(orderList, actual);
        }
    }

    private Order makeOrder() {
        return Order.builder()
                .id(22L)
                .build();
    }

    private Account makeAccount() {
        return Account.builder().id(1L).build();
    }
}