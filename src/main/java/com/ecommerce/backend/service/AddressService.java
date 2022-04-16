package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.entity.Address;
import com.ecommerce.backend.domain.request.AddressRequest;
import com.ecommerce.backend.domain.response.AddressResponse;
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
    public AddressResponse.ReadResponse findById(Long addressId){
        Address address = addressRepository.findById(addressId).orElseThrow(EntityNotFoundException::new);
        return AddressResponse.ReadResponse.fromAddress(address);
    }

    public void save(AddressRequest.RegisterRequest request, String email){
        Account account = accountRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);

        Address address = request.toAddress(account);
        addressRepository.save(address);
    }

    public void update(Long addressId, AddressRequest.UpdateRequest request){
        final Address address = request.toAddress(addressId);

        addressRepository.save(address);
    }

    public AddressResponse.DeleteResponse delete(Long addressId) {
        Address address = addressRepository.findById(addressId).orElseThrow(EntityNotFoundException::new);
        addressRepository.delete(address);
        return AddressResponse.DeleteResponse.fromAddress(address);
    }
}

