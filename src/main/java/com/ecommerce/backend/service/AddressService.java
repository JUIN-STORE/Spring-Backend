package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.entity.Address;
import com.ecommerce.backend.domain.request.AddressRequest;
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
@Service("addressService")
@RequiredArgsConstructor
public class AddressService {
    private final AccountRepository accountRepository;
    private final AddressRepository addressRepository;

    @Transactional(readOnly = true)
    public Address readById(Long addressId){
        return addressRepository.findById(addressId).orElseThrow(EntityNotFoundException::new);
    }

    public void addAddress(Address address){
        addressRepository.save(address);
    }

    public void addAddress(AddressRequest.Register request, String email){
        Account account = accountRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);

        Address address = request.toAddress(account);
        addressRepository.save(address);
    }

    public void update(Long addressId, AddressRequest.Update request){
//        final Address address = request.toAddress(addressId);
//
//        addressRepository.save(address);
    }

    public Address remove(Long addressId) {
        Address address = addressRepository.findById(addressId).orElseThrow(EntityNotFoundException::new);
        addressRepository.delete(address);
        return address;
    }

    public void deleteAccount(Account account) {

    }

    public Address findByAccountId(Long accountId) {
        return addressRepository.findByAccountId(accountId).orElseThrow(EntityNotFoundException::new);
    }

    public void remove(Address address) {
        addressRepository.delete(address);
    }
}

