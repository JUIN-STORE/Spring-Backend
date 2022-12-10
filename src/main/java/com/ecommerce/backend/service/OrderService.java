package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.*;
import com.ecommerce.backend.domain.request.OrderRequest;
import com.ecommerce.backend.domain.response.OrderJoinResponse;
import com.ecommerce.backend.exception.Msg;
import com.ecommerce.backend.repository.jpa.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service("orderService")
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final AddressService addressService;
    private final ProductService productService;
    private final OrderProductService orderProductService;
    private final DeliveryService deliveryService;

    public Page<OrderJoinResponse> read(Account account, OrderRequest.Read request, Pageable pageable) {
        return orderRepository
                .findOrderJoinOrderProductJoinProductByAccountId(account.getId(), request, pageable)
                .orElse(new PageImpl<>(new ArrayList<>()));
    }

    @Transactional
    public Order addOrder(Account account, OrderRequest.Create request) {
        // 엔티티 조회
        Address deliveryAddress;

        if (request.getDeliveryAddress() == null) {
            throw new InvalidParameterException(Msg.ORDER_DELIVERY_ADDRESS_NOT_FOUND);
        }
        if (request.getDeliveryAddress().isDefaultAddress()) {
            deliveryAddress = addressService.readByAccountIdAndDefaultAddress(account.getId());
        } else {
            deliveryAddress = addressService.addIfNull(account, request.getDeliveryAddress());
        }

        if (request.getDeliveryReceiver() == null) {
            throw new InvalidParameterException(Msg.ORDER_DELIVERY_RECEIVER_REQUIRED);
        }
        final DeliveryReceiver deliveryReceiver = request.getDeliveryReceiver().toDeliveryReceiver();

        // 배송 정보 생성
        final Delivery delivery = Delivery.createDelivery(deliveryReceiver, deliveryAddress);
        deliveryService.add(delivery); // update product 쿼리 날아감. (오류)

        final List<Long> productIdList = request.getProductIdList();
        List<Product> productList = productService.readByIdList(productIdList);

        final List<OrderProduct> orderProductList = new ArrayList<>();
        for (Product product : productList) {
            final OrderProduct orderProduct
                    = OrderProduct.createOrderProduct(product, request.getCount(), product.getPrice() * request.getCount());
            orderProductList.add(orderProduct);
            orderProductService.add(orderProduct);
        }

        // 주문 상품 생성
        final Order order = Order.createOrder(account, delivery, orderProductList);
        orderRepository.save(order);

        return order;
    }

    public List<Order> readByAccountId(Long accountId) {
        return orderRepository
                .findByAccountId(accountId)
                .orElse(new ArrayList<>());
    }

    // 주문 취소
    @Transactional
    public void cancel(Long orderId, Long accountId) {
        // 주문 엔티티 조회
        final Order order = readByIdAndAccountId(orderId, accountId);
        order.cancel();
    }

    public long removeByAccountId(Long accountId) {
        return orderRepository.deleteByAccountId(accountId);
    }

    private Order readByIdAndAccountId(Long orderId, Long accountId) {
        return orderRepository
                .findByIdAndAccountId(orderId, accountId)
                .orElseThrow(() -> new EntityNotFoundException(Msg.ORDER_NOT_FOUND));
    }
}
