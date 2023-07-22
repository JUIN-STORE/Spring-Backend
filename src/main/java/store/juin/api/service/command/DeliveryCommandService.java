package store.juin.api.service.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import store.juin.api.domain.entity.Delivery;
import store.juin.api.handler.CommandTransactional;
import store.juin.api.repository.jpa.DeliveryRepository;

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