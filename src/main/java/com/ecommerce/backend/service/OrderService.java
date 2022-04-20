package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.*;
import com.ecommerce.backend.domain.request.OrderRequest;
import com.ecommerce.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

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
    private final AccountRepository accountRepository;
    private final AddressRepository addressRepository;
    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final DeliveryRepository deliveryRepository;

    private final ProductService productService;

    public Order order(OrderRequest.CreateRequest request, String email){
        // 엔티티 조회
        final Account account = accountRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
        final Address address = addressRepository.findByAccountId(account.getId()).orElseThrow(EntityNotFoundException::new);
        final Product product = productService.findById(request.getProductId());

        // 배송 정보 생성
        final Delivery delivery = Delivery.createDelivery(address);
        System.out.println(delivery.toString());

        // 주문 상품 생성
        final OrderProduct orderProduct = OrderProduct.createOrderProduct(product, request.getCount(), product.getPrice() * request.getCount());
        final Order order = Order.createOrder(account, delivery, orderProduct);
        
        // 수정해야 되는 엔티티
        deliveryRepository.save(delivery); // update product 쿼리 날아감. (오류)
        orderRepository.save(order);
        orderProductRepository.save(orderProduct);

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
