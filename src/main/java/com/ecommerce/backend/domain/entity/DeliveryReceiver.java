package com.ecommerce.backend.domain.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;

@Builder
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class DeliveryReceiver {
    @NotBlank
    private String receiverName;

    @NotBlank
    private String receiverPhoneNumber;

    @NotBlank
    private String receiverEmail;

}