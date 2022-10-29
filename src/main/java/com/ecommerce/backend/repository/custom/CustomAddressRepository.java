package com.ecommerce.backend.repository.custom;

import com.ecommerce.backend.domain.entity.Address;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomAddressRepository {
    Optional<Address> findByAccountIdAndDefaultAddress(Long accountId);
}
