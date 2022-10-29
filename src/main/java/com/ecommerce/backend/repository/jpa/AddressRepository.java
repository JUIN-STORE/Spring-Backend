package com.ecommerce.backend.repository.jpa;

import com.ecommerce.backend.domain.entity.Address;
import com.ecommerce.backend.repository.custom.CustomAddressRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long>, CustomAddressRepository {
    Long deleteByAccountId(Long id);
    Optional<List<Address>> findByAccountId(Long id);
}
