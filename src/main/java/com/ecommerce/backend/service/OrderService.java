package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.*;
import com.ecommerce.backend.domain.request.OrderRequest;
import com.ecommerce.backend.domain.response.OrderJoinResponse;
import com.ecommerce.backend.repository.jpa.DeliveryRepository;
import com.ecommerce.backend.repository.jpa.OrderProductRepository;
import com.ecommerce.backend.repository.jpa.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service("orderService")
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final DeliveryRepository deliveryRepository;

    private final AccountService accountService;
    private final AddressService addressService;
    private final ProductService productService;

    public List<OrderJoinResponse> join(Principal principal) {
        final Account account = accountService.readByPrincipal(principal);
        return orderRepository.findOrderJoinOrderProductJoinProductByAccountId(account.getId());
    }

    @Transactional
    public Order addOrder(OrderRequest.Create request, String email) {
        // 엔티티 조회
        final Account account = accountService.readByEmail(email);
        // FIXME: List<Address> 받아야 됨.
        final Address address = addressService.readByAccountId(account.getId()).get(0);
        final List<Long> productIdList = request.getProductIdList();
        final List<Product> productList = productIdList.stream().map(productService::readByProductId).collect(Collectors.toList());

        // 배송 정보 생성
        final Delivery delivery = Delivery.createDelivery(address);

        deliveryRepository.save(delivery); // update product 쿼리 날아감. (오류)

        final List<OrderProduct> orderProductList = new ArrayList<>();

        for (Product product : productList) {
            final OrderProduct orderProduct = OrderProduct.createOrderProduct(product, request.getCount(), product.getPrice() * request.getCount());
            orderProductList.add(orderProduct);
            orderProductRepository.save(orderProduct);
        }

        // 주문 상품 생성
        final Order order = Order.createOrder(account, delivery, orderProductList);

        orderRepository.save(order);

        return order;
    }

    // 주문 취소
    public void cancelOrder (Long orderId){
        // 주문 엔티티 조회
        final Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
        final OrderProduct orderProduct = orderProductRepository.findByOrderId(order.getId()).orElseThrow(EntityNotFoundException::new);
        order.cancel();
        orderRepository.save(order);
//        orderProductRepository.delete(orderProduct);
    }
}
