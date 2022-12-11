package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.OrderProduct;
import com.ecommerce.backend.exception.Msg;
import com.ecommerce.backend.repository.jpa.OrderProductRepository;
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
class OrderProductServiceTest {
    @Mock
    private OrderProductRepository mockOrderProductRepository;

    @InjectMocks
    private OrderProductService sut;

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

            given(mockOrderProductRepository.deleteByOrderIdList(orderIdList)).willReturn(result);

            // when
            long actual = sut.removeByOrderIdList(orderIdList);

            // then
            assertEquals(result, actual);
            verify(mockOrderProductRepository, times(1)).deleteByOrderIdList(orderIdList);
        }
    }

    @Nested
    @DisplayName("주문하기")
    class AddTest {
        @Test
        @DisplayName("성공")
        void addTest01() {
            // given
            var orderProduct = new OrderProduct();
            given(mockOrderProductRepository.save(orderProduct)).willReturn(orderProduct);

            // when
            sut.add(orderProduct);

            // then
            verify(mockOrderProductRepository, times(1)).save(orderProduct);
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
            var orderProduct = new OrderProduct();
            given(mockOrderProductRepository.findByOrderId(orderId)).willReturn(Optional.of(orderProduct));

            // when
            final OrderProduct actual = sut.readByOrderId(orderId);

            // then
            assertEquals(orderProduct, actual);
        }

        @Test
        @DisplayName("존재하지 않는 주문 제품")
        void readByOrderIdTest02() {
            // given
            var orderId = 3L;
            given(mockOrderProductRepository.findByOrderId(orderId)).willThrow(new EntityNotFoundException(Msg.ORDER_PRODUCT_NOT_FOUND));

            // when
            AbstractThrowableAssert<?, ? extends Throwable> actual = assertThatThrownBy(() -> sut.readByOrderId(orderId));

            // then
            actual.isInstanceOf(EntityNotFoundException.class).hasMessage(Msg.ORDER_PRODUCT_NOT_FOUND);
        }
    }
}