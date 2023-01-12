package com.ecommerce.backend.service.command;

import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.entity.Address;
import com.ecommerce.backend.domain.request.AddressRequest;
import com.ecommerce.backend.repository.jpa.AddressRepository;
import com.ecommerce.backend.service.query.AddressQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressCommandService {
    private final AddressRepository addressRepository;

    private final AddressQueryService addressQueryService;

    public Address add(Address address) {
        return addressRepository.save(address);
    }

    @Transactional
    public Address add(Account account, AddressRequest.Register request) {
        final Address address = request.toAddress(account);

        return add(address);
    }

    public Address addIfNull(Account account, AddressRequest.Register addressRegister) {
        final Address address = addressQueryService.readByAccountIdAndZipCodeAndCityAndStreet(account.getId(), addressRegister);

        if (address == null) {
            return add(account, addressRegister);
        } else {
            return address;
        }
    }

    @Transactional
    public Address modify(Account account, AddressRequest.Update request) {
        // address 있는지 확인
        final Address oldAddress = addressQueryService.readByIdAndAccountId(request.getAddressId(), account.getId());

        // 있으면 변경
        final Address newAddress = request.toAddress(account);
        oldAddress.dirtyChecking(newAddress);

        return newAddress;
    }

    @Transactional
    public long remove(Long accountId, Long addressId) {
        return addressRepository.delete(accountId, addressId);
    }

    public long removeByAddressIdList(Long accountId, List<Long> addressIdList) {
        return addressRepository.deleteByAddressIdList(accountId, addressIdList);
    }
}

