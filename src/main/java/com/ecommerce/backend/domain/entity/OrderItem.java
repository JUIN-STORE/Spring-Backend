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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

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

//
//    private void setQuantity(Integer quantity) {
//        this.quantity = quantity;
//    }
//
//    private void setPrice(Integer price) {
//        this.price = price;
//    }
//
//    //** 비즈니스 로직 **   삭제 예정임 !!!!!//
////    public void cancel(){
////        getProduct().addQuantity(quantity);
////    }
//
//    public void setProduct(Product product) {
//        this.product = product;
//    }
//
//
//
//    public void setOrder(Order order){
//        this.order = order;
//    }
}
