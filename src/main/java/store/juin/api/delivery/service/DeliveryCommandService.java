package store.juin.api.delivery.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import store.juin.api.common.handler.CommandTransactional;
import store.juin.api.delivery.model.entity.Delivery;
import store.juin.api.delivery.repository.jpa.DeliveryRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryCommandService {
    private final CommandTransactional commandTransactional;

    private final DeliveryRepository deliveryRepository;

    public void add(Delivery delivery) {
        commandTransactional.execute(() ->
                deliveryRepository.save(delivery)
        );
    }

    public long removeByAddressIdList(List<Long> addressIdList) {
        return commandTransactional.execute(() ->
                deliveryRepository.deleteByAddressIdIn(addressIdList)
        );
    }
}