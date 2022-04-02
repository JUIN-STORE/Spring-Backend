package com.ecommerce.backend.domain.entity;

import com.ecommerce.backend.domain.enums.DeliveryStatus;
import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Delivery {
    @Id
    @Column(name = "delivery_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String city;

    private String street;

    private String zipCode;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus;

    @Setter
    @OneToOne(mappedBy = "delivery") // mappedBy가 있으면 연관관계 주인이 아님.
    private Order order;

}