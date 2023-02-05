package store.juin.api.repository.jpa;

import store.juin.api.domain.entity.Delivery;
import store.juin.api.repository.querydsl.QuerydslDeliveryRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long>, QuerydslDeliveryRepository {
}
