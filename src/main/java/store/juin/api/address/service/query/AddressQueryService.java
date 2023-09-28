package store.juin.api.address.service.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import store.juin.api.address.model.entity.Address;
import store.juin.api.address.model.request.AddressCreateRequest;
import store.juin.api.address.repository.jpa.AddressRepository;
import store.juin.api.common.exception.Msg;
import store.juin.api.common.handler.QueryTransactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressQueryService {
    private final QueryTransactional queryTransactional;

    private final AddressRepository addressRepository;

    public Address readById(Long addressId) {
        return queryTransactional.execute(() ->
                addressRepository.findById(addressId)
                        .orElseThrow(() -> new EntityNotFoundException(Msg.ADDRESS_NOT_FOUND))
        );
    }

    public Address readByIdAndAccountId(Long addressId, Long accountId) {
        return queryTransactional.execute(() ->
                addressRepository.findByIdAndAccountId(addressId, accountId)
                        .orElseThrow(() -> new EntityNotFoundException(Msg.ADDRESS_NOT_FOUND))
        );
    }

    public List<Address> readAllByAccountId(Long accountId) {
        return queryTransactional.execute(() ->
                addressRepository.findAllByAccountId(accountId)
                        .orElseThrow(() -> new EntityNotFoundException(Msg.ADDRESS_NOT_FOUND))
        );
    }

    public Address readByAccountIdAndDefaultAddress(Long accountId) {
        return queryTransactional.execute(() ->
                addressRepository.findByAccountIdAndDefaultAddress(accountId)
                        .orElseThrow(() -> new EntityNotFoundException(Msg.ADDRESS_NOT_FOUND))
        );
    }

    public Address readByAccountIdAndZipCodeAndCityAndStreet(Long accountId, AddressCreateRequest addressAddressCreateRequest) {
        return queryTransactional.execute(() ->
                addressRepository.findByAccountIdAndZipCodeAndCityAndStreet(accountId, addressAddressCreateRequest)
        );
    }
}

