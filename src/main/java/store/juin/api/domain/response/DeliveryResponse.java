package store.juin.api.domain.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import store.juin.api.domain.entity.Delivery;
import store.juin.api.domain.enums.DeliveryStatus;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class DeliveryResponse {
    @Data
    @Accessors(chain = true)
    public static class Read {

        private Long deliveryId;
        private String receiverEmail;
        private String receiverName;
        private String receiverPhoneNumber;
        private DeliveryStatus deliveryStatus;
        private Long addressId;
        private String city;
        private String street;
        private Integer zipCode;

        public static DeliveryResponse.Read from(Delivery delivery) {
            return new DeliveryResponse.Read()
                    .setDeliveryId(delivery.getId())
                    .setReceiverEmail(delivery.getDeliveryReceiver().getReceiverEmail())
                    .setReceiverName(delivery.getDeliveryReceiver().getReceiverName())
                    .setReceiverPhoneNumber(delivery.getDeliveryReceiver().getReceiverPhoneNumber())
                    .setDeliveryStatus(delivery.getDeliveryStatus())
                    .setAddressId(delivery.getDeliveryAddress().getId())
                    .setCity(delivery.getDeliveryAddress().getCity())
                    .setStreet(delivery.getDeliveryAddress().getStreet())
                    .setZipCode(delivery.getDeliveryAddress().getZipCode());
        }
    }
}
