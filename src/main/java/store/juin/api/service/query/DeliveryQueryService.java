package store.juin.api.service.query;

import store.juin.api.domain.entity.Delivery;
import store.juin.api.domain.entity.Order;
import store.juin.api.exception.Msg;
import store.juin.api.repository.jpa.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.security.InvalidParameterException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryQueryService {
    private final DeliveryRepository deliveryRepository;

    @Transactional(readOnly = true)
    public List<Delivery> readAllByAddressIdList(List<Long> addressIdList) {
        return deliveryRepository.findAllByAddressIdIn(addressIdList)
                .orElseThrow(EntityNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public Delivery readById(Long deliveryId, Long accountId) {
        final Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new EntityNotFoundException(Msg.DELIVERY_NOT_FOUND));
        final Order order = delivery.getOrder();

        if (order.getAccount() == null || !order.getAccount().getId().equals(accountId)) {
            throw new InvalidParameterException(Msg.DELIVERY_INVALID_REQUEST);
        }

        return delivery;
    }
}
