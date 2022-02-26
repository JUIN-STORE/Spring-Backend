package com.ecommerce.backend.domain.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
//@Table (name = "ORDER_ITEM", schema = "SHOP")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "orders_id")
    private Order order;

    private String sku;

    private Integer price;

    private Integer discountPrice;

    private Integer quantity;

    @Column(columnDefinition = "TEXT")
    private String content;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    //** 생성 매서드 **//
    public static OrderItem createOrderItem(Product product, Integer price, Integer quantity){
        OrderItem orderItem = OrderItem.builder()
            .product(product)
            .price(price)
            .quantity(quantity)
            .content("d")
            .discountPrice(11).build();
        product.removeQuantity(quantity);

        return orderItem;
    }

    private void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    private void setPrice(Integer price) {
        this.price = price;
    }

    //** 비즈니스 로직 **   삭제 예정임 !!!!!//
//    public void cancel(){
//        getProduct().addQuantity(quantity);
//    }
    public Integer getTotalPrice(){
        return getPrice()*getQuantity();
    }

    public void setProduct(Product product) {
        this.product = product;
    }



    public void setOrder(Order order){
        this.order = order;
    }
}
