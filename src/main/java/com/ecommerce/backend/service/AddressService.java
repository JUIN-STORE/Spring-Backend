package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.Account;
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
public class AddressService {
    private final AddressRepository addressRepository;

    @Transactional(readOnly = true)
    public Address readByIdAndAccountId(Long addressId, Long accountId) {
        return addressRepository.findByIdAndAccountId(addressId, accountId)
                .orElseThrow(EntityNotFoundException::new);
    }

    public Address add(Address address) {
        return addressRepository.save(address);
    }

    public Address add(Account account, AddressRequest.Register request) {
        final Address address = request.toAddress(account);

        return addressRepository.save(address);
    }

    public void modify(Account account, AddressRequest.Update request) {
        final Address address = request.toAddress(account);

        addressRepository.save(address);
    }

    public Address remove(Long addressId) {
        Address address = this.readById(addressId);
        addressRepository.delete(address);
        return address;
    }

    public long removeByAddressIdList(List<Long> addressIdList) {
        return addressRepository.removeByAddressIdList(addressIdList);
    }

    public List<Address> readByAccountId(Long accountId) {
        return addressRepository.findByAccountId(accountId)
                .orElseThrow(() -> new EntityNotFoundException(Msg.ADDRESS_NOT_FOUND));
    }

    public Address readByAccountIdAndDefaultAddress(Long accountId) {
        return addressRepository.findByAccountIdAndDefaultAddress(accountId)
                .orElseThrow(() -> new EntityNotFoundException(Msg.ADDRESS_NOT_FOUND));
    }

    public Address readByAccountIdAndZipCodeAndCityAndStreet(Account account, AddressRequest.Register addressRegister) {
        final Address address =
                addressRepository.findByAccountIdAndZipCodeAndCityAndStreet(account.getId(), addressRegister);

        if (address == null) {
            return add(account, addressRegister);
        } else {
            return address;
        }
    }

    public Address readById(Long addressId) {
        return addressRepository.findById(addressId)
                .orElseThrow(() -> new EntityNotFoundException(Msg.ADDRESS_NOT_FOUND));
    }
}

