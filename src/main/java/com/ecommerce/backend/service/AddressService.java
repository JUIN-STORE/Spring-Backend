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
    public Address readById(Long addressId){
        return addressRepository.findById(addressId)
                .orElseThrow(EntityNotFoundException::new);
    }

    public Address addAddress(Address address){
        return addressRepository.save(address);
    }

    public Address addAddress(Account account, AddressRequest.Register request){
        final Address address = request.toAddress(account);

        return addressRepository.save(address);
    }

    public void update(AddressRequest.Update request){
        final Address address = request.toAddress();

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
}

