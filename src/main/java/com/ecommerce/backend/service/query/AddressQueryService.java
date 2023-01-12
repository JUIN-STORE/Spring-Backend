package com.ecommerce.backend.service.query;

import com.ecommerce.backend.domain.entity.Address;
import com.ecommerce.backend.domain.request.AddressRequest;
import com.ecommerce.backend.exception.Msg;
import com.ecommerce.backend.repository.jpa.AddressRepository;
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

