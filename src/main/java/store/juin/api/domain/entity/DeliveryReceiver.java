package store.juin.api.domain.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@Builder
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class DeliveryReceiver {
    @Column(nullable = false)
    private String receiverName;

    @Column(nullable = false)
    private String receiverPhoneNumber;

    @Column(nullable = false)
    private String receiverEmail;
}