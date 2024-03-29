package store.juin.api.order.service.command;

import org.assertj.core.api.AbstractThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import store.juin.api.address.model.entity.Address;
import store.juin.api.address.model.request.AddressCreateRequest;
import store.juin.api.address.service.command.AddressCommandService;
import store.juin.api.address.service.query.AddressQueryService;
import store.juin.api.cartitem.service.CartItemCommandService;
import store.juin.api.common.exception.Msg;
import store.juin.api.common.handler.CommandTransactional;
import store.juin.api.delivery.model.entity.Delivery;
import store.juin.api.delivery.model.request.DeliveryReceiverRequest;
import store.juin.api.delivery.service.DeliveryCommandService;
import store.juin.api.item.enumeration.NotEnoughStockException;
import store.juin.api.item.model.entity.Item;
import store.juin.api.item.service.query.ItemQueryService;
import store.juin.api.order.enumeration.OrderStatus;
import store.juin.api.order.model.entity.Order;
import store.juin.api.order.model.request.OrderCreateRequest;
import store.juin.api.order.model.response.OrderDeleteResponse;
import store.juin.api.order.repository.jpa.OrderRepository;
import store.juin.api.order.service.query.OrderQueryService;
import store.juin.api.orderitem.service.command.OrderItemCommandService;

import javax.persistence.EntityNotFoundException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;
import static store.juin.api.common.EntityUtil.makeAccount;

@ExtendWith(MockitoExtension.class)
class OrderCommandServiceTest {
    @InjectMocks
    private OrderCommandService sut;

    @Spy
    private CommandTransactional commandTransactional;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ItemQueryService itemQueryService;
    @Mock
    private OrderQueryService orderQueryService;
    @Mock
    private AddressQueryService addressQueryService;

    @Mock
    private AddressCommandService addressCommandService;
    @Mock
    private CartItemCommandService cartItemCommandService;
    @Mock
    private DeliveryCommandService deliveryCommandService;
    @Mock
    private OrderItemCommandService orderItemCommandService;

    @Nested
    @DisplayName("주문")
    class addTest {
        @Test
        @DisplayName("성공 (기본 주소일 때)")
        void addTest01() {
            // given
            var account = makeAccount("asdq13@#13$");
            var itemIdList = new ArrayList<Long>();
            itemIdList.add(1L);

            var itemList = new ArrayList<Item>();
            var item = Item.builder()
                    .id(1L)
                    .name("name")
                    .price(10000)
                    .quantity(1)
                    .soldCount(1)
                    .build();
            itemList.add(item);

            var request = new OrderCreateRequest()
                    .setCount(1)
                    .setDeliveryAddress(new AddressCreateRequest().setDefaultAddress(true))
                    .setDeliveryReceiver(new DeliveryReceiverRequest())
                    .setOrderStatus(OrderStatus.ORDER)
                    .setItemIdList(itemIdList);

            var address = makeAddress();
            var order = makeOrder();

            given(addressQueryService.readByAccountIdAndDefaultAddress(account.getId())).willReturn(address);
            willDoNothing().given(deliveryCommandService).add(any());
            given(itemQueryService.readAllByIdList(anyList())).willReturn(itemList);
            willDoNothing().given(orderItemCommandService).add(any());
            given(orderRepository.save(any())).willReturn(order);

            // when
            sut.add(account, request);

            // then
            verify(addressQueryService, times(1)).readByAccountIdAndDefaultAddress(account.getId());
            verify(deliveryCommandService, times(1)).add(any());
            verify(itemQueryService, times(1)).readAllByIdList(anyList());
            verify(orderRepository, times(1)).save(any());
        }

        @Test
        @DisplayName("성공 (기본 주소가 아닐 때)")
        void addTest02() {
            // given
            var account = makeAccount("asdq13@#13$");
            var itemIdList = new ArrayList<Long>();
            itemIdList.add(1L);

            var itemList = new ArrayList<Item>();
            var item = Item.builder()
                    .id(1L)
                    .name("name")
                    .price(10000)
                    .quantity(1)
                    .soldCount(1)
                    .build();
            itemList.add(item);

            var request = new OrderCreateRequest()
                    .setCount(1)
                    .setDeliveryAddress(new AddressCreateRequest().setDefaultAddress(false))
                    .setDeliveryReceiver(new DeliveryReceiverRequest())
                    .setOrderStatus(OrderStatus.ORDER)
                    .setItemIdList(itemIdList);

            var address = makeAddress();
            var order = makeOrder();

            given(addressCommandService.addIfNull(account, request.getDeliveryAddress())).willReturn(address);
            willDoNothing().given(deliveryCommandService).add(any());
            given(itemQueryService.readAllByIdList(anyList())).willReturn(itemList);
            willDoNothing().given(orderItemCommandService).add(any());
            given(orderRepository.save(any())).willReturn(order);

            // when
            sut.add(account, request);

            // then
            verify(addressCommandService, times(1)).addIfNull(account, request.getDeliveryAddress());
            verify(deliveryCommandService, times(1)).add(any());
            verify(itemQueryService, times(1)).readAllByIdList(anyList());
            verify(orderRepository, times(1)).save(any());
        }

        @Test
        @DisplayName("배송지 누락으로 주문 실패")
        void addTest03() {
            // given
            var account = makeAccount("asdq13@#13$");
            var itemIdList = new ArrayList<Long>();
            itemIdList.add(1L);

            var request = new OrderCreateRequest()
                    .setCount(1)
                    .setDeliveryAddress(null)
                    .setDeliveryReceiver(new DeliveryReceiverRequest())
                    .setOrderStatus(OrderStatus.ORDER)
                    .setItemIdList(itemIdList);

            // when
            AbstractThrowableAssert<?, ? extends Throwable> actual = assertThatThrownBy(() -> sut.add(account, request));

            // then
            actual.isInstanceOf(InvalidParameterException.class).hasMessage(Msg.ORDER_DELIVERY_ADDRESS_NOT_FOUND);
        }

        @Test
        @DisplayName("받는 사람 정보 누락으로 주문 실패")
        void addTest04() {
            // given
            var account = makeAccount("asdq13@#13$");
            var itemIdList = new ArrayList<Long>();
            itemIdList.add(1L);

            var request = new OrderCreateRequest()
                    .setCount(1)
                    .setDeliveryAddress(new AddressCreateRequest())
                    .setDeliveryReceiver(null)
                    .setOrderStatus(OrderStatus.ORDER)
                    .setItemIdList(itemIdList);

            // when
            AbstractThrowableAssert<?, ? extends Throwable> actual = assertThatThrownBy(() -> sut.add(account, request));

            // then
            actual.isInstanceOf(InvalidParameterException.class).hasMessage(Msg.ORDER_DELIVERY_RECEIVER_REQUIRED);
        }

        @Test
        @DisplayName("재고가 없을 경우")
        void addTest05() {
            // given
            var account = makeAccount("asdq13@#13$");
            var itemIdList = new ArrayList<Long>();
            itemIdList.add(1L);

            var itemList = new ArrayList<Item>();
            var item = Item.builder()
                    .id(1L)
                    .name("name")
                    .price(10000)
                    .quantity(0)
                    .soldCount(1)
                    .build();
            itemList.add(item);


            var request = new OrderCreateRequest()
                    .setCount(1)
                    .setDeliveryAddress(new AddressCreateRequest().setDefaultAddress(true))
                    .setDeliveryReceiver(new DeliveryReceiverRequest())
                    .setOrderStatus(OrderStatus.ORDER)
                    .setItemIdList(itemIdList);

            var address = makeAddress();
            var restQuantity = item.getQuantity() - request.getCount();

            given(addressQueryService.readByAccountIdAndDefaultAddress(account.getId())).willReturn(address);
            willDoNothing().given(deliveryCommandService).add(any());
            given(itemQueryService.readAllByIdList(anyList())).willReturn(itemList);

            // when
            AbstractThrowableAssert<?, ? extends Throwable> actual = assertThatThrownBy(() -> sut.add(account, request));

            // then
            actual.isInstanceOf(NotEnoughStockException.class).hasMessage("Need More Stock. Current Stock: " + restQuantity);
        }
    }

    @Nested
    @DisplayName("주문 취소")
    class CancelOrder {
        @Test
        @DisplayName("성공")
        void cancelTest01() {
            // given
            var orderId = 1L;
            var accountId = 1L;
            Order order = Order.builder()
                    .delivery(Delivery.builder().build())
                    .orderStatus(OrderStatus.ORDER).build();

            given(orderQueryService.readByIdAndAccountId(orderId, accountId)).willReturn(order);

            // when
            sut.cancel(orderId, accountId);

            // then
            verify(orderQueryService, times(1)).readByIdAndAccountId(orderId, accountId);
        }

        @Test
        @DisplayName("존재하지 않는 주문")
        void cancelTest02() {
            // given
            var orderId = 1L;
            var accountId = 1L;

            given(orderQueryService.readByIdAndAccountId(orderId, accountId))
                    .willThrow(new EntityNotFoundException(Msg.ORDER_NOT_FOUND));

            // when
            AbstractThrowableAssert<?, ? extends Throwable> actual
                    = assertThatThrownBy(() -> sut.cancel(orderId, accountId));

            // then
            actual.isInstanceOf(EntityNotFoundException.class).hasMessage(Msg.ORDER_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("회원 주문 내역 삭제")
    class RemoveByAccountId {
        @Test
        @DisplayName("성공")
        void removeByAccountId() {
            // given
            var accountId = 1L;
            var result = 1L;
            given(orderRepository.deleteByAccountId(accountId)).willReturn(result);

            // when
            long actual = sut.removeByAccountId(accountId);

            // then
            assertEquals(result, actual);
            verify(orderRepository, times(1)).deleteByAccountId(accountId);
        }
    }

    @Nested
    @DisplayName("계정 아이디로 주문 정보 삭제")
    class RemoveTest {
        @Test
        @DisplayName("성공")
        void removeTest01() {
            // given
            var expected = 1L;
            var orderList = List.of(makeOrder());

            given(orderQueryService.readAllByAccountId(anyLong())).willReturn(orderList);
            given(orderItemCommandService.removeByOrderIdList(anyList())).willReturn(expected);
            given(orderRepository.deleteByAccountId(anyLong())).willReturn(expected);

            // when
            final OrderDeleteResponse actual = sut.remove(22L);

            // then
            assertAll(
                    () -> assertEquals(expected, actual.getOrderItemDeletedCount()),
                    () -> assertEquals(expected, actual.getOrdersDeletedCount())
            );
        }
    }

    private Order makeOrder() {
        return Order.builder().build();
    }

    private Address makeAddress() {
        return Address.builder().build();
    }

}