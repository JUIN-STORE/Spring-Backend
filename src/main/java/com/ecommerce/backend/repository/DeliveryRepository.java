package com.ecommerce.backend.repository;

import com.ecommerce.backend.domain.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value = "DELETE " +
                    "FROM delivery " +
                    "WHERE address_id IN (:addressIdList)")
    void deleteByAddressIdList(@Param("addressIdList") List<Long> addressIdList);

    @Query(nativeQuery = true,
            value = "SELECT * " +
                    "FROM delivery " +
                    "WHERE address_id IN (:addressIdList)")
    List<Delivery> findByAddressIdList(@Param("addressIdList") List<Long> addressIdList);
}
