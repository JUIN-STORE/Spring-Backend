package com.ecommerce.backend.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderProduct extends BaseEntity{
    @Id @Column(name = "order_product_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int orderPrice;         // 주문 가격

    private int orderCount;         // 주몬 수량

    // 연관관계 주인 -> fillProduct 만들어야 됨.
    @JoinColumn(name = "product_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    // 연관관계 주인 -> fillOrderRelation 만들어야 됨.
    @JoinColumn(name = "orders_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    // orderProduct 생성
    public static OrderProduct createOrderProduct(Product product , int orderCount, int orderPrice){
        OrderProduct orderProduct = OrderProduct.builder()
                                .product(product)
                                .orderPrice(orderPrice)
                                .orderCount(orderCount)
                                .build();

        product.removeQuantity(orderCount);

        return orderProduct;
    }

    public void fillOrderRelation(Order order) {
        // 기존 Order와의 연관관계 제거
        if (this.order != null) this.order.getOrderProductList().remove(this);

        this.order = order;

        // 무한루프 빠지지 않도록 처리
        if (!order.getOrderProductList().contains(this)) order.getOrderProductList().add(this);
    }

    // 주문 취소
    public void cancel(){
        getProduct().addQuantity(orderCount);
    }
    
}