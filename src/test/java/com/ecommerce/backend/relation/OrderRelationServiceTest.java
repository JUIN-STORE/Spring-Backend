//package com.ecommerce.backend.relation;
//
//import com.ecommerce.backend.domain.entity.Order;
//import com.ecommerce.backend.domain.response.OrderResponse;
//import com.ecommerce.backend.relation.OrderRelationService;
//import com.ecommerce.backend.service.query.OrderItemQueryService;
//import com.ecommerce.backend.service.query.OrderQueryService;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.ArrayList;
//
//import static org.junit.jupiter.api.Assertions.assertAll;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.anyList;
//import static org.mockito.BDDMockito.given;
//
//@ExtendWith(MockitoExtension.class)
//class OrderRelationServiceTest {
//    @Mock
//    private OrderQueryService orderQueryService;
//
//    @Mock
//    private OrderItemQueryService orderItemQueryService;
//
//    @InjectMocks
//    private OrderRelationService sut;
//
//    @Nested
//    @DisplayName("계정 아이디로 주문 정보 삭제")
//    class RemoveTest {
//        @Test
//        @DisplayName("성공")
//        void removeTest01() {
//            // given
//            var accountId = 1L;
//            var expected = 1L;
//            var orderList = new ArrayList<Order>();
//            orderList.add(new Order());
//
//            given(orderQueryService.readAllByAccountId(accountId)).willReturn(orderList);
//            given(orderItemQueryService.removeByOrderIdList(anyList())).willReturn(expected);
//            given(orderQueryService.removeByAccountId(accountId)).willReturn(expected);
//
//            // when
//            final OrderResponse.Delete actual = sut.remove(accountId);
//
//            // then
//            assertAll(
//                    () -> assertEquals(expected, actual.getOrderItemDeletedCount()),
//                    () -> assertEquals(expected, actual.getOrdersDeletedCount())
//            );
//        }
//    }
//}