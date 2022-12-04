package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.entity.Address;
import com.ecommerce.backend.domain.request.AddressRequest;
import com.ecommerce.backend.exception.Msg;
import com.ecommerce.backend.repository.jpa.AddressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;

    public Address add(Address address) {
        return addressRepository.save(address);
    }

    public Address add(Account account, AddressRequest.Register request) {
        final Address address = request.toAddress(account);

        return add(address);
    }

    public Address addIfNull(Account account, AddressRequest.Register addressRegister) {
        final Address address = readByAccountIdAndZipCodeAndCityAndStreet(account.getId(), addressRegister);

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

    public Address readByIdAndAccountId(Long addressId, Long accountId) {
        return addressRepository.findByIdAndAccountId(addressId, accountId)
                .orElseThrow(() -> new EntityNotFoundException(Msg.ADDRESS_NOT_FOUND));
    }

    public List<Address> readByAccountId(Long accountId) {
        return addressRepository.findByAccountId(accountId)
                .orElseThrow(() -> new EntityNotFoundException(Msg.ADDRESS_NOT_FOUND));
    }

    public Address readByAccountIdAndDefaultAddress(Long accountId) {
        return addressRepository.findByAccountIdAndDefaultAddress(accountId)
                .orElseThrow(() -> new EntityNotFoundException(Msg.ADDRESS_NOT_FOUND));
    }

    public Address readByAccountIdAndZipCodeAndCityAndStreet(Long accountId, AddressRequest.Register addressRegister) {
        return addressRepository.findByAccountIdAndZipCodeAndCityAndStreet(accountId, addressRegister);
    }


    public Address modify(Account account, AddressRequest.Update request) {
        // address 있는지 확인
        this.readByIdAndAccountId(request.getAddressId(), account.getId());

        // 있으면 변경
        final Address address = request.toAddress(account);
        return add(address);
    }


    public long remove(Long accountId, Long addressId) {
        return addressRepository.delete(accountId, addressId);
    }

    public long removeByAddressIdList(Long accountId, List<Long> addressIdList) {
        return addressRepository.deleteByAddressIdList(accountId, addressIdList);
    }
}

