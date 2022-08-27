package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.*;
import com.ecommerce.backend.domain.request.OrderRequest;
import com.ecommerce.backend.repository.AddressRepository;
import com.ecommerce.backend.repository.DeliveryRepository;
import com.ecommerce.backend.repository.OrderProductRepository;
import com.ecommerce.backend.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/** Service Naming
 * C -> save
 * R -> findBy~
 * U -> update
 * D -> delete
 */

@Slf4j
@Service("orderService")
@RequiredArgsConstructor
public class OrderService{
    private final AddressRepository addressRepository;
    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final DeliveryRepository deliveryRepository;

    private final AccountService accountService;
    private final ProductService productService;

    public Order addOrder(OrderRequest.Create request, String email){
        // 엔티티 조회
        final Account account = accountService.readByEmail(email);
        final Address address = addressRepository.findByAccountId(account.getId()).orElseThrow(EntityNotFoundException::new);
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
