package com.ecommerce.backend.domain.entity;

import com.ecommerce.backend.domain.enums.DeliveryStatus;
import com.ecommerce.backend.domain.enums.OrderStatus;
import com.ecommerce.backend.exception.AlreadyDeliveryException;
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
    @Id @Column(name = "order_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus; // 주문 상태

    private LocalDateTime orderDate; // 주문일

    // 연관관계 주인 -> fillAccountRelation 만들어야 됨.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;
    
    // 연관관계 주인 -> fillDeliveryRelation 만들어야 됨.
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    // 읽기 전용 -> addOrderProduct 만들어야 됨.
    @Builder.Default
    @OneToMany(mappedBy = "order",  fetch = FetchType.LAZY)
    private List<OrderProduct> orderProductList = new ArrayList<>();

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
    public void addOrderProduct(OrderProduct orderProduct) {
        orderProductList.add(orderProduct);
        orderProduct.fillOrderRelation(this);
    }

    // 연관관계에 있는 객체들 파라미터로 받기
    public static Order createOrder(Account account, Delivery delivery, OrderProduct... orderProductList) {
        Order order = Order.builder()
                .account(account)
                .delivery(delivery)
                .orderStatus(OrderStatus.ORDER)
                .orderDate(LocalDateTime.now())
                .build();

        // orderProductList.stream().forEach(orderProduct -> order.addOrderProduct(orderProduct));
        for (OrderProduct orderProduct : orderProductList) {
            order.addOrderProduct(orderProduct);
        }

        return order;
    }

    // 주문 취소
    public void cancel(){
        if (delivery.getDeliveryStatus() == DeliveryStatus.COMP) throw new AlreadyDeliveryException("이미 배송된 상품은 취소가 불가능합니다");

        this.orderStatus = OrderStatus.CANCEL;
        for(OrderProduct orderItem : orderProductList){
            orderItem.cancel();
        }
    }

    //  전체 주문 가격 조회
    public int getGrandTotal(){
        int totalPrice  = 0;
        for(OrderProduct orderProduct : orderProductList){
            totalPrice += orderProduct.getOrderPrice();
        }
        return totalPrice;
    }
}
