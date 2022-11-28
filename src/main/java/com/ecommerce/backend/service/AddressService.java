package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.entity.Address;
import com.ecommerce.backend.domain.request.AddressRequest;
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
public class AddressService {
    private final AddressRepository addressRepository;

    @Transactional(readOnly = true)
    public Address readByIdAndAccountId(Long addressId, Long accountId) {
        return addressRepository.findByIdAndAccountId(addressId, accountId)
                .orElseThrow(EntityNotFoundException::new);
    }

    public Address addAddress(Address address) {
        return addressRepository.save(address);
    }

    public Address addAddress(Account account, AddressRequest.Register request) {
        final Address address = request.toAddress(account);

        return addressRepository.save(address);
    }

    public void update(Account account, AddressRequest.Update request) {
        final Address address = request.toAddress(account);

        addressRepository.save(address);
    }

    public Address remove(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(EntityNotFoundException::new);
        addressRepository.delete(address);
        return address;
    }

    public List<Address> readByAccountId(Long accountId) {
        return addressRepository.findByAccountId(accountId)
                .orElseThrow(EntityNotFoundException::new);
    }

    public Address readByAccountIdAndDefaultAddress(Long accountId) {
        return addressRepository.findByAccountIdAndDefaultAddress(accountId)
                .orElseThrow(EntityNotFoundException::new);
    }

    public Address readByAccountIdAndZipCodeAndCityAndStreet(Account account, AddressRequest.Register addressRegister) {
        final Address address =
                addressRepository.findByAccountIdAndZipCodeAndCityAndStreet(account.getId(), addressRegister);

        if (address == null) {
            return addAddress(account, addressRegister);
        } else {
            return address;
        }
    }
}

