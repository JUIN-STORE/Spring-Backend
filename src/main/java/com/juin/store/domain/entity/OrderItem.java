package com.juin.store.domain.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Entity
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem extends BaseEntity {
    @Id @Column(name = "order_item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private int orderPrice;         // 주문 가격

    @NotNull
    private int orderCount;         // 주몬 수량

    // 연관관계 주인 -> fillItem 만들어야 됨.
    @NotNull
    @JoinColumn(name = "item_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Item item;

    // 연관관계 주인 -> fillOrderRelation 만들어야 됨.
    @NotNull
    @JoinColumn(name = "orders_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    // orderItem 생성
    public static OrderItem createOrderItem(Item item, int orderCount, int orderPrice){
        OrderItem orderItem = OrderItem.builder()
                                .item(item)
                                .orderPrice(orderPrice)
                                .orderCount(orderCount)
                                .build();

        item.removeQuantity(orderCount);

        return orderItem;
    }

    public void fillOrderRelation(Order order) {
        // 기존 Order와의 연관관계 제거
        if (this.order != null) this.order.getOrderItemList().remove(this);

        this.order = order;

        // 무한루프 빠지지 않도록 처리
        if (!order.getOrderItemList().contains(this)) order.getOrderItemList().add(this);
    }

    // 주문 취소
    public void cancel(){
        getItem().addQuantity(orderCount);
    }
    
}