package com.ecommerce.backend.service.query;

import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.entity.Order;
import com.ecommerce.backend.domain.request.OrderRequest;
import com.ecommerce.backend.domain.response.OrderJoinResponse;
import com.ecommerce.backend.repository.jpa.OrderRepository;
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
    class ReadTest {
        @Test
        @DisplayName("성공")
        void readTest01() {
            // given
            var page = 0;
            var size = 10;
            var account = makeAccount();
            var request = new OrderRequest.Read();
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
    class ReadByAccountIdTest {
        @Test
        @DisplayName("성공")
        void readByAccountIdTest() {
            // given
            var accountId = 1L;
            var orderList = new ArrayList<Order>();
            orderList.add(new Order());
            given(mockOrderRepository.findAllByAccountId(accountId)).willReturn(Optional.of(orderList));

            // when
            List<Order> actual = sut.readAllByAccountId(accountId);

            // then
            assertEquals(orderList, actual);
        }
    }

    private Account makeAccount() {
        return Account.builder().id(1L).build();
    }
}