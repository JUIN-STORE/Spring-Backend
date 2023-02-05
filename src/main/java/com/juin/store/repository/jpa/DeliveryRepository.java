package com.juin.store.repository.jpa;

import com.juin.store.domain.entity.Delivery;
import com.juin.store.repository.querydsl.QuerydslDeliveryRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long>, QuerydslDeliveryRepository {
}
