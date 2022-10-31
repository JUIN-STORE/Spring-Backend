package com.ecommerce.backend.repository.querydsl;

import com.ecommerce.backend.domain.entity.Address;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuerydslAddressRepository {
    Optional<Address> findByAccountIdAndDefaultAddress(Long accountId);
}
