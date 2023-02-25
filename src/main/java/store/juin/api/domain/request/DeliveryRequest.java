package store.juin.api.domain.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import store.juin.api.domain.entity.DeliveryReceiver;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class DeliveryRequest {
    @Data @Accessors(chain = true)
    public static class Receiver {
        private String receiverName;

        private String receiverPhoneNumber;

        private String receiverEmail;

        public DeliveryReceiver toDeliveryReceiver(){
            return DeliveryReceiver
                    .builder()
                    .receiverName(receiverName)
                    .receiverPhoneNumber(receiverPhoneNumber)
                    .receiverEmail(receiverEmail)
                    .build();
        }
    }
}