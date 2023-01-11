package com.ecommerce.backend.service.command;

import com.ecommerce.backend.domain.entity.Delivery;
import com.ecommerce.backend.repository.jpa.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryCommandService {
    private final DeliveryRepository deliveryRepository;

    public void add(Delivery delivery) {
        deliveryRepository.save(delivery);
    }

    @Transactional
    public long removeByAddressIdList(List<Long> addressIdList) {
        return deliveryRepository.deleteByAddressIdIn(addressIdList);
    }
}