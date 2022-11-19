package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.*;
import com.ecommerce.backend.domain.request.OrderRequest;
import com.ecommerce.backend.domain.response.OrderJoinResponse;
import com.ecommerce.backend.exception.NotOrderException;
import com.ecommerce.backend.repository.jpa.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service("orderService")
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    private final AddressService addressService;
    private final ProductService productService;
    private final OrderProductService orderProductService;
    private final DeliveryService deliveryService;

    public List<OrderJoinResponse> join(Account account) {
        return orderRepository.findOrderJoinOrderProductJoinProductByAccountId(account.getId())
                .orElseThrow(NotOrderException::new);
    }

    @Transactional
    public Order addOrder(Account account, OrderRequest.Create request) {
        // 엔티티 조회
        final Address address = addressService.readByAccountIdAndDefaultAddress(account.getId());
        final List<Long> productIdList = request.getProductIdList();
        final List<Product> productList = productIdList.stream().map(productService::readByProductId).collect(Collectors.toList());
        final DeliveryReceiver deliveryReceiver = request.getDeliveryReceiver();

        // 배송 정보 생성
        final Delivery delivery = Delivery.createDelivery(deliveryReceiver, address);

        deliveryService.add(delivery); // update product 쿼리 날아감. (오류)

        final List<OrderProduct> orderProductList = new ArrayList<>();

        for (Product product : productList) {
            final OrderProduct orderProduct = OrderProduct.createOrderProduct(product, request.getCount(), product.getPrice() * request.getCount());
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
                .orElseThrow(EntityNotFoundException::new);
    }

    public Order readById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
    }

    // 주문 취소
    public void cancelOrder (Long orderId){
        // 주문 엔티티 조회
        final Order order = readById(orderId);
        final OrderProduct orderProduct = orderProductService.readByOrderId(order.getId());

        order.cancel();
        orderRepository.save(order);
    }

    public void removeByAccountId(Long accountId) {
        orderRepository.deleteByAccountId(accountId);
    }
}
