package com.ecommerce.backend.repository;

import com.ecommerce.backend.domain.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    Optional<Address> deleteByAccountId(Long id);
}
