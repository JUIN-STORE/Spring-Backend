package store.juin.api.delivery.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import store.juin.api.delivery.model.entity.Delivery;
import store.juin.api.delivery.repository.querydsl.QuerydslDeliveryRepository;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long>, QuerydslDeliveryRepository {
}
