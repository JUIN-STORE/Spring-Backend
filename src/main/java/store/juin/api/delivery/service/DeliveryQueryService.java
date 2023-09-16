package store.juin.api.delivery.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import store.juin.api.common.exception.Msg;
import store.juin.api.common.handler.QueryTransactional;
import store.juin.api.delivery.model.entity.Delivery;
import store.juin.api.delivery.repository.jpa.DeliveryRepository;
import store.juin.api.order.model.entity.Order;

import javax.persistence.EntityNotFoundException;
import java.security.InvalidParameterException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryQueryService {
    private final QueryTransactional queryTransactional;

    private final DeliveryRepository deliveryRepository;

    public Delivery readById(Long deliveryId) {
        return queryTransactional.execute(() ->
                deliveryRepository.findById(deliveryId)
                        .orElseThrow(() -> new EntityNotFoundException(Msg.DELIVERY_NOT_FOUND))
        );
    }

    public List<Delivery> readAllByAddressIdList(List<Long> addressIdList) {
        return queryTransactional.execute(() ->
                deliveryRepository.findAllByAddressIdIn(addressIdList)
                        .orElseThrow(EntityNotFoundException::new)
        );
    }

    public Delivery readById(Long deliveryId, Long accountId) {
        final Delivery delivery = readById(deliveryId);

        checkValidDelivery(delivery, accountId);
        return delivery;
    }

    private void checkValidDelivery(Delivery delivery, Long accountId) {
        final Order order = delivery.getOrder();

        if (order.getAccount() == null || !order.getAccount().getId().equals(accountId)) {
            throw new InvalidParameterException(Msg.DELIVERY_INVALID_REQUEST);
        }
    }
}
