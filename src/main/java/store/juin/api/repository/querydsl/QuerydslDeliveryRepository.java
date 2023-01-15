package store.juin.api.repository.querydsl;

import store.juin.api.domain.entity.Delivery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuerydslDeliveryRepository {
    long deleteByAddressIdIn(List<Long> addressIdList);

    Optional<List<Delivery>> findAllByAddressIdIn(List<Long> addressIdList);
}