package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.entity.Address;
import com.ecommerce.backend.domain.request.AddressRequest;
import com.ecommerce.backend.domain.response.AddressResponse;
import com.ecommerce.backend.domain.response.AddressResponse.AddressRead;
import com.ecommerce.backend.repository.AccountRepository;
import com.ecommerce.backend.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

/** Service Naming
 * C -> save
 * R -> findBy~
 * U -> update
 * D -> delete
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressService {
    private final AccountRepository accountRepository;
    private final AddressRepository addressRepository;

    @Transactional(readOnly = true)
    public AddressRead findById(Long addressId){
        Address address = addressRepository.findById(addressId).orElseThrow(EntityNotFoundException::new);
        return AddressRead.fromAddress(address);
    }

    public AddressResponse.AddressCreate save(Long accountId, AddressRequest.RegisterAddress request){
        Account account = accountRepository.findById(accountId).orElseThrow(EntityNotFoundException::new);

        Address address = request.toAddress(account);
        addressRepository.save(address);
        return AddressResponse.AddressCreate.fromAddress(address);
    }

    public AddressResponse.AddressDelete delete(Long addressId) {
        Address address = addressRepository.findById(addressId).orElseThrow(EntityNotFoundException::new);
        addressRepository.delete(address);
        return AddressResponse.AddressDelete.fromAddress(address);
    }
}

