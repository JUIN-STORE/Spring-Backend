package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.entity.Address;
import com.ecommerce.backend.domain.mapper.AddressMapper;
import com.ecommerce.backend.domain.request.AddressRequest;
import com.ecommerce.backend.domain.response.AddressRes;
import com.ecommerce.backend.domain.response.AddressRes.AddressReadRes;
import com.ecommerce.backend.exception.AccountNotFoundException;
import com.ecommerce.backend.repository.AccountRepository;
import com.ecommerce.backend.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

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
    private final AddressMapper addressMapper;

    @Transactional(readOnly = true)
    public AddressReadRes findById(long id){
        Address address = addressRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return addressMapper.addressToReadRes(address);
    }

    @Transactional
    public AddressRes.AddressCreateRes save(Long accountId, AddressRequest.RegisterAddress request){
        Optional<Account> account = accountRepository.findById(accountId);

        if (account.isPresent()) {
            Address address = addressMapper.createRequestToEntity(request);
            addressRepository.save(address);
            return addressMapper.entityToCreateRes(address);
        } else{
            throw new AccountNotFoundException("해당하는 계정이 존재하지 않습니다.");
        }
    }
}

