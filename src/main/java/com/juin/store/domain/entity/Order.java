package com.juin.store.domain.entity;

import com.juin.store.domain.enums.DeliveryStatus;
import com.juin.store.domain.enums.OrderStatus;
import com.juin.store.exception.AlreadyDeliveryException;
import com.juin.store.exception.Msg;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@SuperBuilder
@Entity(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {
    @Id @Column(name = "orders_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus; // 주문 상태

    @NotNull
    private LocalDateTime orderDate; // 주문일

    // 연관관계 주인 -> fillAccountRelation 만들어야 됨.
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;
    
    // 연관관계 주인 -> fillDeliveryRelation 만들어야 됨.
    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    // 읽기 전용 -> addOrderItem 만들어야 됨.
    @Builder.Default
    @OneToMany(mappedBy = "order",  fetch = FetchType.LAZY)
    private List<OrderItem> orderItemList = new ArrayList<>();

    // 연관관계 주인 -> Account 쓰기 전용
    public void fillAccountRelation(Account account) {
        if (this.account != null) this.account.getOrderList().remove(this);
        this.account = account;
        account.getOrderList().add(this);
    }

    /** Delivery 연관관계 설정, @OneToOne -> 연관관계 주인
     * @param delivery
     */
    public void fillDeliveryRelation(Delivery delivery) {
        this.delivery = delivery;
    }
    
    // 읽기 전용
    public void addOrderItem(OrderItem orderItem) {
        orderItemList.add(orderItem);
        orderItem.fillOrderRelation(this);
    }

    // 연관관계에 있는 객체들 파라미터로 받기
    public static Order createOrder(Account account, Delivery delivery) {
        return Order.builder()
                .account(account)
                .delivery(delivery)
                .orderStatus(OrderStatus.ORDER)
                .orderDate(LocalDateTime.now())
                .build();
    }

    // 주문 취소
    public void cancel(){
        if (delivery.getDeliveryStatus() == DeliveryStatus.COMP)
            throw new AlreadyDeliveryException(Msg.ORDER_ALREADY_DELIVERY);

        this.orderStatus = OrderStatus.CANCEL;
        for(OrderItem orderItem : orderItemList){
            orderItem.cancel();
        }
    }

    //  전체 주문 가격 조회
    public int getGrandTotal(){
        int totalPrice  = 0;
        for(OrderItem orderItem : orderItemList){
            totalPrice += orderItem.getOrderPrice();
        }
        return totalPrice;
    }
}
