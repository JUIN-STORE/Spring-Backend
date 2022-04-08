package com.ecommerce.backend.domain.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem extends BaseEntity{
    @Id @Column(name = "order_item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 연관관계 주인
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    // 연관관계 주인
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orders_id")
    private Order order;

    private int orderPrice;

    private int orderCount;

    // orderItem 생성
    public static OrderItem createOrderItem(Product product, int orderPrice, int orderCount){
        OrderItem orderItem = OrderItem.builder()
                                .product(product)
                                .orderPrice(orderPrice)
                                .orderCount(orderCount)
                                .build();

        product.removeQuantity(orderCount);

        return orderItem;
    }

    public Integer getTotalPrice(){
        return this.orderPrice * this.orderCount;
    }

    public void cancel(){
        getProduct().addQuantity(orderCount);
    }
}
