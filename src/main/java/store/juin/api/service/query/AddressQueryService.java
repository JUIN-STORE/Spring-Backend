package store.juin.api.service.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import store.juin.api.domain.entity.Address;
import store.juin.api.domain.request.AddressRequest;
import store.juin.api.exception.Msg;
import store.juin.api.handler.QueryTransactional;
import store.juin.api.repository.jpa.AddressRepository;

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

    public Address readByAccountIdAndZipCodeAndCityAndStreet(Long accountId, AddressRequest.Create addressCreate) {
        return queryTransactional.execute(() ->
                addressRepository.findByAccountIdAndZipCodeAndCityAndStreet(accountId, addressCreate)
        );
    }
}

