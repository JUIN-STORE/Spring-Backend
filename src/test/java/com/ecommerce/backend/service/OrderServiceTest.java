package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.*;
import com.ecommerce.backend.domain.enums.OrderStatus;
import com.ecommerce.backend.domain.request.AddressRequest;
import com.ecommerce.backend.domain.request.DeliveryRequest;
import com.ecommerce.backend.domain.request.OrderRequest;
import com.ecommerce.backend.domain.response.OrderJoinResponse;
import com.ecommerce.backend.exception.Msg;
import com.ecommerce.backend.exception.NotEnoughStockException;
import com.ecommerce.backend.repository.jpa.OrderRepository;
import org.assertj.core.api.AbstractThrowableAssert;
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

import javax.persistence.EntityNotFoundException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository mockOrderRepository;

    @Mock
    private AddressService mockAddressService;

    @Mock
    private ProductService mockProductService;

    @Mock
    private OrderProductService mockOrderProductService;

    @Mock
    private DeliveryService mockDeliveryService;

    @InjectMocks
    private OrderService sut;

    @Nested
    @DisplayName("주문 상세 조회")
    class ReadTest {
        @Test
        @DisplayName("성공")
        void readTest01() {
            // given
            var page = 0;
            var size = 10;
            var account = getAccount();
            var request = new OrderRequest.Read();
            var pageRequest = PageRequest.of(page, size);

            var orderJoinResponseList = new ArrayList<OrderJoinResponse>();
            orderJoinResponseList.add(new OrderJoinResponse());
            var responsePage = new PageImpl<>(orderJoinResponseList);

            given(mockOrderRepository.findOrderJoinOrderProductJoinProductByAccountId(account.getId(), request, pageRequest))
                    .willReturn(Optional.of(responsePage));

            // when
            Page<OrderJoinResponse> actual = sut.read(account, request, pageRequest);

            // then
            assertEquals(orderJoinResponseList, actual.getContent());
        }
    }

    @Nested
    @DisplayName("주문")
    class AddOrderTest {
        @Test
        @DisplayName("성공 (기본 주소일 때)")
        void addOrderTest01() {
            // given
            var account = getAccount();
            var productIdList = new ArrayList<Long>();
            productIdList.add(1L);

            var productList = new ArrayList<Product>();
            var product = Product.builder()
                    .id(1L)
                    .productName("productName")
                    .price(10000)
                    .quantity(1)
                    .soldCount(1)
                    .build();
            productList.add(product);

            var request = new OrderRequest.Create()
                    .setCount(1)
                    .setDeliveryAddress(new AddressRequest.Register().setDefaultAddress(true))
                    .setDeliveryReceiver(new DeliveryRequest.Receiver())
                    .setOrderStatus(OrderStatus.ORDER)
                    .setProductIdList(productIdList);

            var address = new Address();
            var order = new Order();

            given(mockAddressService.readByAccountIdAndDefaultAddress(account.getId())).willReturn(address);
            willDoNothing().given(mockDeliveryService).add(any());
            given(mockProductService.readAllByIdList(anyList())).willReturn(productList);
            willDoNothing().given(mockOrderProductService).add(any());
            given(mockOrderRepository.save(any())).willReturn(order);

            // when
            sut.addOrder(account, request);

            // then
            verify(mockAddressService, times(1)).readByAccountIdAndDefaultAddress(account.getId());
            verify(mockDeliveryService, times(1)).add(any());
            verify(mockProductService, times(1)).readAllByIdList(anyList());
            verify(mockOrderRepository, times(1)).save(any());
        }

        @Test
        @DisplayName("성공 (기본 주소가 아닐 때)")
        void addOrderTest02() {
            // given
            var account = getAccount();
            var productIdList = new ArrayList<Long>();
            productIdList.add(1L);

            var productList = new ArrayList<Product>();
            var product = Product.builder()
                    .id(1L)
                    .productName("productName")
                    .price(10000)
                    .quantity(1)
                    .soldCount(1)
                    .build();
            productList.add(product);

            var request = new OrderRequest.Create()
                    .setCount(1)
                    .setDeliveryAddress(new AddressRequest.Register().setDefaultAddress(false))
                    .setDeliveryReceiver(new DeliveryRequest.Receiver())
                    .setOrderStatus(OrderStatus.ORDER)
                    .setProductIdList(productIdList);

            var address = new Address();
            var order = new Order();

            given(mockAddressService.addIfNull(account, request.getDeliveryAddress())).willReturn(address);
            willDoNothing().given(mockDeliveryService).add(any());
            given(mockProductService.readAllByIdList(anyList())).willReturn(productList);
            willDoNothing().given(mockOrderProductService).add(any());
            given(mockOrderRepository.save(any())).willReturn(order);

            // when
            sut.addOrder(account, request);

            // then
            verify(mockAddressService, times(1)).addIfNull(account, request.getDeliveryAddress());
            verify(mockDeliveryService, times(1)).add(any());
            verify(mockProductService, times(1)).readAllByIdList(anyList());
            verify(mockOrderRepository, times(1)).save(any());
        }

        @Test
        @DisplayName("배송지 누락으로 주문 실패")
        void addOrderTest03() {
            // given
            var account = getAccount();
            var productIdList = new ArrayList<Long>();
            productIdList.add(1L);

            var request = new OrderRequest.Create()
                    .setCount(1)
                    .setDeliveryAddress(null)
                    .setDeliveryReceiver(new DeliveryRequest.Receiver())
                    .setOrderStatus(OrderStatus.ORDER)
                    .setProductIdList(productIdList);

            // when
            AbstractThrowableAssert<?, ? extends Throwable> actual = assertThatThrownBy(() -> sut.addOrder(account, request));

            // then
            actual.isInstanceOf(InvalidParameterException.class).hasMessage(Msg.ORDER_DELIVERY_ADDRESS_NOT_FOUND);
        }

        @Test
        @DisplayName("받는 사람 정보 누락으로 주문 실패")
        void addOrderTest04() {
            // given
            var account = getAccount();
            var productIdList = new ArrayList<Long>();
            productIdList.add(1L);

            var request = new OrderRequest.Create()
                    .setCount(1)
                    .setDeliveryAddress(new AddressRequest.Register())
                    .setDeliveryReceiver(null)
                    .setOrderStatus(OrderStatus.ORDER)
                    .setProductIdList(productIdList);

            // when
            AbstractThrowableAssert<?, ? extends Throwable> actual = assertThatThrownBy(() -> sut.addOrder(account, request));

            // then
            actual.isInstanceOf(InvalidParameterException.class).hasMessage(Msg.ORDER_DELIVERY_RECEIVER_REQUIRED);
        }

        @Test
        @DisplayName("재고가 없을 경우")
        void addOrderTest05() {
            // given
            var account = getAccount();
            var productIdList = new ArrayList<Long>();
            productIdList.add(1L);

            var productList = new ArrayList<Product>();
            var product = Product.builder()
                    .id(1L)
                    .productName("productName")
                    .price(10000)
                    .quantity(0)
                    .soldCount(1)
                    .build();
            productList.add(product);


            var request = new OrderRequest.Create()
                    .setCount(1)
                    .setDeliveryAddress(new AddressRequest.Register().setDefaultAddress(true))
                    .setDeliveryReceiver(new DeliveryRequest.Receiver())
                    .setOrderStatus(OrderStatus.ORDER)
                    .setProductIdList(productIdList);

            var address = new Address();
            var restQuantity = product.getQuantity() - request.getCount();

            given(mockAddressService.readByAccountIdAndDefaultAddress(account.getId())).willReturn(address);
            willDoNothing().given(mockDeliveryService).add(any());
            given(mockProductService.readAllByIdList(anyList())).willReturn(productList);

            // when
            AbstractThrowableAssert<?, ? extends Throwable> actual = assertThatThrownBy(() -> sut.addOrder(account, request));

            // then
            actual.isInstanceOf(NotEnoughStockException.class).hasMessage("Need More Stock. Current Stock: " + restQuantity);
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
            given(mockOrderRepository.findByAccountId(accountId)).willReturn(Optional.of(orderList));

            // when
            List<Order> actual = sut.readByAccountId(accountId);

            // then
            assertEquals(orderList, actual);
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
                    .delivery(new Delivery())
                    .orderStatus(OrderStatus.ORDER).build();
            given(mockOrderRepository.findByIdAndAccountId(orderId, accountId)).willReturn(Optional.of(order));

            // when
            sut.cancel(orderId, accountId);

            // then
            verify(mockOrderRepository, times(1)).findByIdAndAccountId(orderId, accountId);
        }

        @Test
        @DisplayName("존재하지 않는 주문")
        void cancelTest02() {
            // given
            var orderId = 1L;
            var accountId = 1L;
            Order order = Order.builder().orderStatus(OrderStatus.ORDER).build();

            given(mockOrderRepository.findByIdAndAccountId(orderId, accountId))
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
            given(mockOrderRepository.deleteByAccountId(accountId)).willReturn(result);

            // when
            long actual = sut.removeByAccountId(accountId);

            // then
            assertEquals(result, actual);
            verify(mockOrderRepository, times(1)).deleteByAccountId(accountId);
        }
    }

    private Account getAccount() {
        return Account.builder().id(1L).build();
    }
}