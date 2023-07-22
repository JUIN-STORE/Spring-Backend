package store.juin.api.service.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import store.juin.api.domain.entity.Delivery;
import store.juin.api.domain.entity.Order;
import store.juin.api.exception.Msg;
import store.juin.api.handler.QueryTransactional;
import store.juin.api.repository.jpa.DeliveryRepository;

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
