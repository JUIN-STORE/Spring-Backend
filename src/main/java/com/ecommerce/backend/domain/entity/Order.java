package com.ecommerce.backend.domain.entity;

import com.ecommerce.backend.domain.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "orders")
public class Order extends BaseEntity {
    @Id
    @Column(name = "order_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus; // 주문 상태

    private LocalDateTime orderDate; // 주문일

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL
            , orphanRemoval = true, fetch = FetchType.LAZY)
    public List<OrderItem> orderItemList = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    //** 연관 관계 메서드 **//
//    public void setAccount(Account account) {
//        if (this.account != null){
//            this.account.getOrderList().remove(this);
//        }
//        this.account = account;
//        account.getOrderList().add(this);
//    }

    public void addOrderItem(OrderItem orderItem) {
        orderItemList.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    public int getTotalPrice() {
        int totalPrice = 0;

        for (OrderItem orderItem : orderItemList) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }

    public static Order createOrder(Account account, List<OrderItem> orderItemList) {
        Order order = Order.builder()
                .account(account)
                .orderStatus(OrderStatus.ORDER)
                .orderDate(LocalDateTime.now())
                .build();

        //  for (OrderItem orderItem : orderItemList) { order.addOrderItem(orderItem); }

        orderItemList.stream().forEach(orderItem -> order.addOrderItem(orderItem));

        return order;
    }
//
//    //** 생성 메서드 **//
//    public  Order (Account account) {
//      this.account = account;
//    }
//
//    //** 비즈니스 로직 **//
//    // 주문 취소
//    public void cancel(){
////        this.setStatus((short)2);
//        this.setStatus(OrderStatus.CANCEL);
//        for(OrderItem orderItem:orderItems){
//            orderItem.cancel();
//        }
//    }
//
//    //  주문 조회
//    public Integer getGrandTotal(){
//        Integer totalPrice  = 0;
//        for(OrderItem orderItem: orderItems){
//            grandTotal += orderItem.getTotalPrice();
//        }
//        return totalPrice;
//    }
//
//    public void setStatus(OrderStatus status){
//        this.status = status;
//    }
}
