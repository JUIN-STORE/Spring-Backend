package store.juin.api.address.repository.querydsl;

import org.springframework.stereotype.Repository;
import store.juin.api.address.model.entity.Address;
import store.juin.api.address.model.request.AddressCreateRequest;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuerydslAddressRepository {
    Optional<Address> findByAccountIdAndDefaultAddress(Long accountId);

    Address findByAccountIdAndZipCodeAndCityAndStreet(Long accountId, AddressCreateRequest addressCreateRequest);

    long delete(Long accountId, Long addressId);

    long deleteByAddressIdList(Long accountId, List<Long> addressIdList);
}
