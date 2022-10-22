package com.ecommerce.backend.repository.jpa;

import com.ecommerce.backend.domain.entity.Delivery;
import com.ecommerce.backend.repository.custom.CustomDeliveryRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long>, CustomDeliveryRepository {
}
