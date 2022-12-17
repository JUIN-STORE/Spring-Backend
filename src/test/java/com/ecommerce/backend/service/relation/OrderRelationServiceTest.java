package com.ecommerce.backend.service.relation;

import com.ecommerce.backend.domain.entity.Order;
import com.ecommerce.backend.domain.response.OrderResponse;
import com.ecommerce.backend.relation.OrderRelationService;
import com.ecommerce.backend.service.OrderItemService;
import com.ecommerce.backend.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrderRelationServiceTest {
    @Mock
    private OrderService orderService;

    @Mock
    private OrderItemService orderItemService;

    @InjectMocks
    private OrderRelationService sut;

    @Nested
    @DisplayName("계정 아이디로 주문 정보 삭제")
    class RemoveTest {
        @Test
        @DisplayName("성공")
        void removeTest01() {
            // given
            var accountId = 1L;
            var expected = 1L;
            var orderList = new ArrayList<Order>();
            orderList.add(new Order());

            given(orderService.readByAccountId(accountId)).willReturn(orderList);
            given(orderItemService.removeByOrderIdList(anyList())).willReturn(expected);
            given(orderService.removeByAccountId(accountId)).willReturn(expected);

            // when
            final OrderResponse.Delete actual = sut.remove(accountId);

            // then
            assertAll(
                    () -> assertEquals(expected, actual.getOrderItemDeletedCount()),
                    () -> assertEquals(expected, actual.getOrdersDeletedCount())
            );
        }
    }
}