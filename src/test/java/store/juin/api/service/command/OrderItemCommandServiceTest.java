package store.juin.api.service.command;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import store.juin.api.domain.entity.OrderItem;
import store.juin.api.handler.CommandTransactional;
import store.juin.api.repository.jpa.OrderItemRepository;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderItemCommandServiceTest {
    @InjectMocks
    private OrderItemCommandService sut;

    @Spy
    private CommandTransactional commandTransactional;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Nested
    @DisplayName("removeByOrderIdList 테스트")
    class RemoveByOrderIdListTest {
        @Test
        @DisplayName("주문 아이디 리스트로 삭제 성공")
        void removeByOrderIdListTest01() {
            // given
            var result = 1L;
            var orderIdList = new ArrayList<Long>();
            orderIdList.add(1L);

            given(orderItemRepository.deleteByOrderIdList(orderIdList)).willReturn(result);

            // when
            long actual = sut.removeByOrderIdList(orderIdList);

            // then
            assertEquals(result, actual);
            verify(orderItemRepository, times(1)).deleteByOrderIdList(orderIdList);
        }
    }

    @Nested
    @DisplayName("add 테스트")
    class AddTest {
        @Test
        @DisplayName("주문 성공")
        void addTest01() {
            // given
            var orderItem = makeOrderItem();
            given(orderItemRepository.save(orderItem)).willReturn(orderItem);

            // when
            sut.add(orderItem);

            // then
            verify(orderItemRepository, times(1)).save(orderItem);
        }
    }

    private OrderItem makeOrderItem() {
        return OrderItem.builder()
                .id(1L)
                .build();

    }
}