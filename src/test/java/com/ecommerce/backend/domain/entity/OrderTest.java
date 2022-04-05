package com.ecommerce.backend.domain.entity;

import com.ecommerce.backend.domain.enums.AccountRole;
import com.ecommerce.backend.repository.AccountRepository;
import com.ecommerce.backend.repository.OrderItemRepository;
import com.ecommerce.backend.repository.OrderRepository;
import com.ecommerce.backend.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest
class OrderTest {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @PersistenceContext
    private EntityManager em;

    @Test
    @DisplayName("지연 로딩 테스트")
    public void lazyLoadingTest(){
        Order order = this.createOrder();
        Long orderItemId = order.getOrderItemList().get(0).getId();

        em.flush();
        em.clear();

        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(EntityNotFoundException::new);
        System.out.println("Order class: " + orderItem.getOrder().getClass());
        System.out.println("===================================");
        orderItem.getOrder().getOrderDate();
        System.out.println("===================================");
    }

    @Test
    @DisplayName("영속성 전이 테스트")
    public void cascadeTest(){
        Order order = new Order();

        for (int i = 0; i < 3; i++){
            Product product = this.createProduct();
            productRepository.save(product);
            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .orderPrice(10)
                    .orderPrice(100)
                    .order(order)
                    .build();

            order.getOrderItemList().add(orderItem);
        }

        orderRepository.save(order);
        em.clear();

        Order saveOrder = orderRepository.findById(order.getId()).orElseThrow(EntityNotFoundException::new);
        assertEquals(3, saveOrder.getOrderItemList().size());
    }

    @Test
    @DisplayName("고아객체 제거 테스트")
    public void orphanRemovalTest(){
        Order order = this.createOrder();
        order.getOrderItemList().remove(0);
        em.flush();
    }

    public Product createProduct(){
        return Product.builder()
                .productName("테스트 상품")
                .price(10000)
                .quantity(100)
                .soldCount(10)
                .description("테스트 상품입니다.")
                .thumbnailPath("/thumb")
                .originImagePath("/origin")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public Order createOrder(){
        Order order = new Order();

        for (int i = 0; i < 3; i++) {
            Product product = this.createProduct();
            productRepository.save(product);
            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .orderPrice(10)
                    .orderPrice(100)
                    .order(order)
                    .build();

            order.getOrderItemList().add(orderItem);
        }


        Account account = Account.builder()
                .email("cart@test.com")
                .passwordHash("test")
                .name("릴러말즈")
                .accountRole(AccountRole.USER)
                .lastLogin(LocalDateTime.now())
                .build();

        accountRepository.save(account);

//        order.setAccount(account);
        orderRepository.save(order);
        return order;
    }
}