package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.OrderItem;
import com.ecommerce.backend.exception.Msg;
import com.ecommerce.backend.repository.jpa.OrderItemRepository;
import org.assertj.core.api.AbstractThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderItemServiceTest {
    @Mock
    private OrderItemRepository mockOrderItemRepository;

    @InjectMocks
    private OrderItemService sut;

    @Nested
    @DisplayName("주문 아이디 리스트로 삭제")
    class RemoveByOrderIdListTest {
        @Test
        @DisplayName("성공")
        void removeByOrderIdListTest01() {
            // given
            var result = 1L;
            var orderIdList = new ArrayList<Long>();
            orderIdList.add(1L);

            given(mockOrderItemRepository.deleteByOrderIdList(orderIdList)).willReturn(result);

            // when
            long actual = sut.removeByOrderIdList(orderIdList);

            // then
            assertEquals(result, actual);
            verify(mockOrderItemRepository, times(1)).deleteByOrderIdList(orderIdList);
        }
    }

    @Nested
    @DisplayName("주문하기")
    class AddTest {
        @Test
        @DisplayName("성공")
        void addTest01() {
            // given
            var orderItem = new OrderItem();
            given(mockOrderItemRepository.save(orderItem)).willReturn(orderItem);

            // when
            sut.add(orderItem);

            // then
            verify(mockOrderItemRepository, times(1)).save(orderItem);
        }
    }

    @Nested
    @DisplayName("주문 아이디로 주문 제품 조회하기")
    class ReadByOrderIdTest {
        @Test
        @DisplayName("성공")
        void readByOrderIdTest01() {
            // given
            var orderId = 2L;
            var orderItem = new OrderItem();
            given(mockOrderItemRepository.findByOrderId(orderId)).willReturn(Optional.of(orderItem));

            // when
            final OrderItem actual = sut.readByOrderId(orderId);

            // then
            assertEquals(orderItem, actual);
        }

        @Test
        @DisplayName("존재하지 않는 주문 제품")
        void readByOrderIdTest02() {
            // given
            var orderId = 3L;
            given(mockOrderItemRepository.findByOrderId(orderId)).willThrow(new EntityNotFoundException(Msg.ORDER_ITEM_NOT_FOUND));

            // when
            AbstractThrowableAssert<?, ? extends Throwable> actual = assertThatThrownBy(() -> sut.readByOrderId(orderId));

            // then
            actual.isInstanceOf(EntityNotFoundException.class).hasMessage(Msg.ORDER_ITEM_NOT_FOUND);
        }
    }
}