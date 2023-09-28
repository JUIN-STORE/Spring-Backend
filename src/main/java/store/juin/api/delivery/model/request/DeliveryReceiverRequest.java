package store.juin.api.delivery.model.request;

import lombok.Data;
import lombok.experimental.Accessors;
import store.juin.api.delivery.model.entity.DeliveryReceiver;

@Data
@Accessors(chain = true)
public class DeliveryReceiverRequest {
    private String receiverName;

    private String receiverPhoneNumber;

    private String receiverEmail;

    public DeliveryReceiver toDeliveryReceiver() {
        return DeliveryReceiver
                .builder()
                .receiverName(receiverName)
                .receiverPhoneNumber(receiverPhoneNumber)
                .receiverEmail(receiverEmail)
                .build();
    }
}