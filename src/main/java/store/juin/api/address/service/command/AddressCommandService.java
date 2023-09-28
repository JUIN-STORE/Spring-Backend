package store.juin.api.address.service.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import store.juin.api.account.model.entity.Account;
import store.juin.api.address.model.entity.Address;
import store.juin.api.address.model.request.AddressCreateRequest;
import store.juin.api.address.model.request.AddressUpdateRequest;
import store.juin.api.address.repository.jpa.AddressRepository;
import store.juin.api.address.service.query.AddressQueryService;
import store.juin.api.common.handler.CommandTransactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressCommandService {
    private final CommandTransactional commandTransactional;

    private final AddressRepository addressRepository;

    private final AddressQueryService addressQueryService;

    public Address add(Address address) {
        return commandTransactional.execute(() ->
                addressRepository.save(address)
        );
    }

    public Address add(Account account, AddressCreateRequest request) {
        final Address address = request.toAddress(account);

        return add(address);
    }

    public Address addIfNull(Account account, AddressCreateRequest request) {
        final Address address = addressQueryService.readByAccountIdAndZipCodeAndCityAndStreet(account.getId(), request);

        if (address == null) {
            return add(account, request);
        } else {
            return address;
        }
    }

    public Address modify(Account account, AddressUpdateRequest request) {
        // address 있는지 확인
        final Address oldAddress = addressQueryService.readByIdAndAccountId(request.getAddressId(), account.getId());

        return commandTransactional.execute(() -> {
            // 있으면 변경
            final Address newAddress = request.toAddress(account);
            oldAddress.dirtyChecking(newAddress);

            return newAddress;
        });
    }

    public long remove(Long accountId, Long addressId) {
        return commandTransactional.execute(() ->
                addressRepository.delete(accountId, addressId)
        );
    }

    public long removeByAddressIdList(Long accountId, List<Long> addressIdList) {
        return addressRepository.deleteByAddressIdList(accountId, addressIdList);
    }
}

