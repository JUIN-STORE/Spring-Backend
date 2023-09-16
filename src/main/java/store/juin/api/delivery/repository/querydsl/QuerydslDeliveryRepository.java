package store.juin.api.delivery.repository.querydsl;

import org.springframework.stereotype.Repository;
import store.juin.api.delivery.model.entity.Delivery;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuerydslDeliveryRepository {
    long deleteByAddressIdIn(List<Long> addressIdList);

    Optional<List<Delivery>> findAllByAddressIdIn(List<Long> addressIdList);
}
