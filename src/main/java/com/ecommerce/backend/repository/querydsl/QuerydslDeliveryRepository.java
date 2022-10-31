package com.ecommerce.backend.repository.querydsl;

import com.ecommerce.backend.domain.entity.Delivery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuerydslDeliveryRepository {
    Long deleteByAddressIdIn(List<Long> addressIdList);

    Optional<List<Delivery>> findByAddressIdIn(List<Long> addressIdList);
}
