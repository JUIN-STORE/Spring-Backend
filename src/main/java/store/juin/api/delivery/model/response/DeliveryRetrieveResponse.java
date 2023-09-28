package store.juin.api.delivery.model.response;

import lombok.Data;
import lombok.experimental.Accessors;
import store.juin.api.delivery.enumeration.DeliveryStatus;
import store.juin.api.delivery.model.entity.Delivery;

@Data
@Accessors(chain = true)
public class DeliveryRetrieveResponse {
    private Long deliveryId;

    private String receiverEmail;

    private String receiverName;

    private String receiverPhoneNumber;

    private DeliveryStatus deliveryStatus;

    private Long addressId;

    private String city;

    private String street;

    private Integer zipCode;

    public static DeliveryRetrieveResponse from(Delivery delivery) {
        return new DeliveryRetrieveResponse()
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
