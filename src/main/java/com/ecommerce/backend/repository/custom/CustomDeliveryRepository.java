package com.ecommerce.backend.repository.custom;

import com.ecommerce.backend.domain.entity.Delivery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomDeliveryRepository {
    Long deleteByAddressIdList(List<Long> addressIdList);

    Optional<List<Delivery>> findByAddressIdList(List<Long> addressIdList);
}
