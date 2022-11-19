package com.ecommerce.backend.domain.entity;

import javax.persistence.Embeddable;

@Embeddable
public class DeliveryReceiver {
    private String receiverName;
    private String receiverPhoneNumber;
    private String receiverEmail;
}