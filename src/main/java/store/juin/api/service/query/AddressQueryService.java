package store.juin.api.service.query;

import store.juin.api.domain.entity.Address;
import store.juin.api.domain.request.AddressRequest;
import store.juin.api.exception.Msg;
import store.juin.api.repository.jpa.AddressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressQueryService {
    private final AddressRepository addressRepository;

    @Transactional(readOnly = true)
    public Address readById(Long addressId) {
        return addressRepository.findById(addressId)
                .orElseThrow(() -> new EntityNotFoundException(Msg.ADDRESS_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Address readByIdAndAccountId(Long addressId, Long accountId) {
        return addressRepository.findByIdAndAccountId(addressId, accountId)
                .orElseThrow(() -> new EntityNotFoundException(Msg.ADDRESS_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<Address> readAllByAccountId(Long accountId) {
        return addressRepository.findAllByAccountId(accountId)
                .orElseThrow(() -> new EntityNotFoundException(Msg.ADDRESS_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Address readByAccountIdAndDefaultAddress(Long accountId) {
        return addressRepository.findByAccountIdAndDefaultAddress(accountId)
                .orElseThrow(() -> new EntityNotFoundException(Msg.ADDRESS_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Address readByAccountIdAndZipCodeAndCityAndStreet(Long accountId, AddressRequest.Register addressRegister) {
        return addressRepository.findByAccountIdAndZipCodeAndCityAndStreet(accountId, addressRegister);
    }
}

