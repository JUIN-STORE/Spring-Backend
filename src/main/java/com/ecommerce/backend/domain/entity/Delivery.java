package com.ecommerce.backend.domain.entity;

import com.ecommerce.backend.domain.enums.DeliveryStatus;
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
public class Delivery {
    @Id @Column(name = "delivery_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String city;

    private String street;

    private Integer zipCode;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus;

    @JoinColumn(name = "address_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Address address;

    @OneToOne(mappedBy = "delivery") // mappedBy가 있으면 연관관계 주인이 아님.
    private Order order;

    public static Delivery createDelivery(Address address) {
        return Delivery.builder()
                .address(address)
                .city(address.getCity())
                .street(address.getStreet())
                .zipCode(address.getZipCode())
                .deliveryStatus(DeliveryStatus.READY)
                .build();
    }
}