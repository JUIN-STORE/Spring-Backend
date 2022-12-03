package com.ecommerce.backend.repository.querydsl;

import com.ecommerce.backend.domain.entity.Address;
import com.ecommerce.backend.domain.request.AddressRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuerydslAddressRepository {
    Optional<Address> findByAccountIdAndDefaultAddress(Long accountId);

    Address findByAccountIdAndZipCodeAndCityAndStreet(Long accountId, AddressRequest.Register register);

    long removeByAddressIdList(List<Long> addressIdList);
}
