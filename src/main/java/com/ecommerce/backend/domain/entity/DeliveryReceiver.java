package com.ecommerce.backend.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryReceiver {
    private String receiverName;

    private String receiverPhoneNumber;

    private String receiverEmail;

    public static DeliveryReceiver createDeliveryReceiver(String receiverName,
                                                  String receiverPhoneNumber,
                                                  String receiverEmail) {
        return DeliveryReceiver.builder()
                .receiverName(receiverName)
                .receiverPhoneNumber(receiverPhoneNumber)
                .receiverEmail(receiverEmail)
                .build();
    }
}