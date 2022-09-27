package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.Delivery;
import com.ecommerce.backend.repository.jpa.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;

    public void remove(List<Long> addressIdList) {
        deliveryRepository.deleteByAddressIdList(addressIdList);
    }

    public List<Delivery> readByAddressIdList(List<Long> addressIdList) {
        return deliveryRepository.findByAddressIdList(addressIdList);
    }
}
