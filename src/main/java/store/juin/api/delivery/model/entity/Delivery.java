package store.juin.api.delivery.model.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import store.juin.api.address.model.entity.Address;
import store.juin.api.common.model.entity.BaseEntity;
import store.juin.api.delivery.enumeration.DeliveryStatus;
import store.juin.api.order.model.entity.Order;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Entity
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Delivery extends BaseEntity {
    @Id @Column(name = "delivery_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus;

    @Embedded
    private DeliveryReceiver deliveryReceiver;

    @NotNull
    @JoinColumn(name = "address_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Address deliveryAddress;

    @OneToOne(mappedBy = "delivery") // mappedBy가 있으면 연관관계 주인이 아님.
    private Order order;

    public static Delivery createDelivery(DeliveryReceiver deliveryReceiver, Address deliveryAddress) {
        return Delivery.builder()
                .deliveryAddress(deliveryAddress)
                .deliveryReceiver(deliveryReceiver)
                .deliveryStatus(DeliveryStatus.READY)
                .build();
    }
}