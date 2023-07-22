package store.juin.api.service.query;

import org.assertj.core.api.AbstractThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import store.juin.api.domain.entity.OrderItem;
import store.juin.api.exception.Msg;
import store.juin.api.handler.QueryTransactional;
import store.juin.api.repository.jpa.OrderItemRepository;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrderItemQueryServiceTest {
    @InjectMocks
    private OrderItemQueryService sut;

    @Spy
    private QueryTransactional queryTransactional;

    @Mock
    private OrderItemRepository mockOrderItemRepository;

    @Nested
    @DisplayName("주문 아이디로 주문 제품 조회하기")
    class RetrieveByOrderIdTest {
        @Test
        @DisplayName("성공")
        void readByOrderIdTest01() {
            // given
            var orderId = 2L;
            var orderItem = makeOrderItem();
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

    private OrderItem makeOrderItem() {
        return OrderItem.builder()
                .build();
    }
}