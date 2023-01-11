package com.ecommerce.backend.service.query;

import com.ecommerce.backend.domain.entity.Delivery;
import com.ecommerce.backend.repository.jpa.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
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
}
