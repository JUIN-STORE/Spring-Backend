 package com.ecommerce.backend.repository;

 import com.ecommerce.backend.domain.entity.Delivery;
 import org.springframework.data.jpa.repository.JpaRepository;
 import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
}
