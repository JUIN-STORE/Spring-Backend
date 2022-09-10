package com.ecommerce.backend.repository;

import com.ecommerce.backend.domain.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value = "DELETE FROM orders WHERE account_id=:accountId"
    )
    void deleteByAccountId(@Param("accountId") Long accountId);

    List<Order> findByAccountId(Long accountId);
}
