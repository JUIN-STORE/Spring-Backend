package com.ecommerce.backend.domain.entity;

import lombok.*;

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

    private int orderPrice;

    private int orderCount;

    // 연관관계 주인
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    // 연관관계 주인
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orders_id")
    private Order order;

    // orderProduct 생성
    public static OrderProduct createOrderProduct(Product product, int orderPrice, int orderCount){
        OrderProduct orderProduct = OrderProduct.builder()
                                .product(product)
                                .orderPrice(orderPrice)
                                .orderCount(orderCount)
                                .build();

        product.removeQuantity(orderCount);

        return orderProduct;
    }

    public Integer getTotalPrice(){
        return this.orderPrice * this.orderCount;
    }

    public void cancel(){
        getProduct().addQuantity(orderCount);
    }
}
