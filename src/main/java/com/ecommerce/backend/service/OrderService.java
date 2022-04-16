package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.entity.Order;
import com.ecommerce.backend.domain.entity.OrderProduct;
import com.ecommerce.backend.domain.entity.Product;
import com.ecommerce.backend.domain.request.OrderRequest;
import com.ecommerce.backend.repository.AccountRepository;
import com.ecommerce.backend.repository.OrderRepository;
import com.ecommerce.backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

/** Service Naming
 * C -> save
 * R -> findBy~
 * U -> update
 * D -> delete
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService{
    private final AccountRepository accountRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public Long order(OrderRequest.OrderCreate request, String email){
        Product product = productRepository.findById(request.getProductId()).orElseThrow(EntityNotFoundException::new);
        Account account = accountRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);

        List<OrderProduct> orderProductList = new ArrayList<>();

        OrderProduct.createOrderProduct(product, product.getPrice(), request.getCount());

        Order order = Order.createOrder(account, orderProductList);

        orderRepository.save(order);

        return order.getId();
    }
}
