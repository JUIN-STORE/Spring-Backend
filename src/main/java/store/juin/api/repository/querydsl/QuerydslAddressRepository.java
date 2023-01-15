package store.juin.api.repository.querydsl;

import store.juin.api.domain.entity.Address;
import store.juin.api.domain.request.AddressRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuerydslAddressRepository {
    Optional<Address> findByAccountIdAndDefaultAddress(Long accountId);

    Address findByAccountIdAndZipCodeAndCityAndStreet(Long accountId, AddressRequest.Register register);

    long delete(Long accountId, Long addressId);

    long deleteByAddressIdList(Long accountId, List<Long> addressIdList);
}