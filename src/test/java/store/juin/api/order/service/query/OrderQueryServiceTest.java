package store.juin.api.order.service.query;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import store.juin.api.common.handler.QueryTransactional;
import store.juin.api.order.model.entity.Order;
import store.juin.api.order.model.request.OrderRetrieveRequest;
import store.juin.api.order.model.response.OrderJoinResponse;
import store.juin.api.order.repository.jpa.OrderRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static store.juin.api.common.EntityUtil.makeAccount;

@ExtendWith(MockitoExtension.class)
class OrderQueryServiceTest {
    @InjectMocks
    private OrderQueryService sut;

    @Spy
    private QueryTransactional queryTransactional;

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
            var account = makeAccount("asdq13@#13$");
            var request = new OrderRetrieveRequest();
            var pageRequest = PageRequest.of(page, size);

            var orderJoinResponseList = new ArrayList<OrderJoinResponse>();
            orderJoinResponseList.add(new OrderJoinResponse());
            var responsePage = new PageImpl<>(orderJoinResponseList);

            given(mockOrderRepository.findOrderJoinOrderItemJoinItemJoinItemImageByAccountId(account.getId(), request, pageRequest))
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
}