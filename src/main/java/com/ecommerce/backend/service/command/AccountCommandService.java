package com.ecommerce.backend.service.command;

import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.entity.Address;
import com.ecommerce.backend.domain.request.AccountRequest;
import com.ecommerce.backend.domain.response.OrderResponse;
import com.ecommerce.backend.exception.Msg;
import com.ecommerce.backend.repository.jpa.AccountRepository;
import com.ecommerce.backend.service.query.AddressQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityExistsException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountCommandService {
    private final AccountRepository accountRepository;

    private final AddressQueryService addressQueryService;

    private final CartCommandService cartCommandService;
    private final OrderCommandService orderCommandService;
    private final AddressCommandService addressCommandService;
    private final CartItemCommandService cartItemCommandService;
    private final DeliveryCommandService deliveryCommandService;

    @Transactional
    public Account add(AccountRequest.SignUp request) {
        // 이메일 중복 검사
        checkDuplicatedEmail(request);

        final Account account = request.toAccount();
        final Address address = request.getAddress().toAddress(account);

        accountRepository.save(account);

        addressCommandService.add(address);
        cartCommandService.add(account);

        return account;
    }

    private void checkDuplicatedEmail(AccountRequest.SignUp request) {
        Optional<Account> validEmail = accountRepository.findByEmail(request.getEmail());

        if (validEmail.isPresent()) throw new EntityExistsException(Msg.DUPLICATED_ACCOUNT);
    }

    @Transactional
    public Account modify(Account account, AccountRequest.Update request) {
        Account newAccount = request.toAccount(account.getId(), account.getEmail());

        account.updateAccount(newAccount);

        return newAccount;
    }

    @Transactional(rollbackFor = Exception.class)
    public Account remove(Account account, Long accountId) {
        // Address 구하기
        final List<Address> addressList = addressQueryService.readAllByAccountId(account.getId());
        final List<Long> addressIdList = addressList.stream().map(Address::getId).collect(Collectors.toList());

        OrderResponse.Delete deleteResponse = orderCommandService.remove(accountId);

        final long deliveryDeletedCount = deliveryCommandService.removeByAddressIdList(addressIdList);
        final long addressDeletedCount = addressCommandService.removeByAddressIdList(account.getId(), addressIdList);
        final int cartItemDeletedCount = cartItemCommandService.removeByAccountId(account.getId());
        final long cartDeletedCount = cartCommandService.removeByAccountId(account.getId());

        accountRepository.delete(account);  // account 삭제

        log.info("[P9][SERV][ACNT][REMV]: " +
                        "account 삭제 개수:({}), " +
                        "order_item 삭제 개수:({}), " +
                        "orders 삭제 개수:({}), " +
                        "delivery 삭제 개수:({}), " +
                        "address 삭제 개수:({}), " +
                        "cart_item 삭제 개수:({}), " +
                        "cart 삭제 개수:({})"
                , account.getEmail()
                , deleteResponse.getOrderItemDeletedCount()
                , deleteResponse.getOrdersDeletedCount()
                , deliveryDeletedCount
                , addressDeletedCount
                , cartItemDeletedCount
                , cartDeletedCount
        );

        return account;
    }
}
