package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.entity.Address;
import com.ecommerce.backend.domain.entity.Order;
import com.ecommerce.backend.domain.enums.AccountRole;
import com.ecommerce.backend.domain.request.AccountRequest;
import com.ecommerce.backend.exception.Msg;
import com.ecommerce.backend.repository.jpa.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    private final JwtService jwtService;
    private final AddressService addressService;
    private final CartService cartService;
    private final CartProductService cartProductService;
    private final DeliveryService deliveryService;
    private final OrderService orderService;
    private final OrderProductService orderProductService;

    private void checkDuplicatedEmail(AccountRequest.SignUp request) {
        Optional<Account> validEmail = accountRepository.findByEmail(request.getEmail());

        if (validEmail.isPresent()) throw new EntityExistsException("존재하는 이메일입니다. 다른 이메일을 입력해 주세요.");
    }

    @Transactional
    public Account add(AccountRequest.SignUp request) {
        // 이메일 중복 검사
        checkDuplicatedEmail(request);

        final Account account = request.toAccount();
        final Address address = request.getAddress().toAddress(account);

        accountRepository.save(account);

        addressService.add(address);
        cartService.add(account);

        return account;
    }

    @Transactional(readOnly = true)
    public Account readById(Long id) {
        return accountRepository.findById(id) // select * from cart where account_id = ? 쿼리도 나감
                .orElseThrow(() -> new EntityNotFoundException(Msg.ACCOUNT_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Account readByEmail(String email) {
        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(Msg.ACCOUNT_NOT_FOUND));
    }

    @Transactional
    public Account modify(Principal principal, AccountRequest.Update request) {
        final String email = principal.getName();
        final Account oldAccount = this.readByEmail(email);

        Account newAccount = request.toAccount(oldAccount.getId(), email);
        accountRepository.save(newAccount);

        return newAccount;
    }

    @Transactional(rollbackFor = Exception.class)
    public Account remove(Principal principal, Long accountId) {
        final String email = principal.getName();
        final Account account = this.readByIdAndEmail(accountId, email);

        // Address 구하기
        final List<Address> addressList = addressService.readByAccountId(account.getId());
        final List<Long> addressIdList = addressList.stream().map(Address::getId).collect(Collectors.toList());

        final List<Order> orderList = orderService.readByAccountId(account.getId());
        final List<Long> orderIdList = orderList.stream().map(Order::getId).collect(Collectors.toList());

        // FK 걸려있는 데이터들 삭제
        final int orderProductDeletedCount = orderProductService.removeByOrderIdList(orderIdList); // order_product 삭제
        final long ordersDeletedCount = orderService.removeByAccountId(accountId);                  // orders 삭제

        final long deliveryDeletedCount = deliveryService.removeByAddressIdList(addressIdList);    // delivery 삭제
        final long addressDeletedCount =
                addressService.removeByAddressIdList(account.getId(), addressIdList);              // address 삭제

        cartProductService.removeByAccount(account);// cart_product 삭제
        final long cartDeletedCount = cartService.removeByAccountId(account.getId());// cart 삭제

        accountRepository.delete(account);                          // account 삭제

        log.info("[P9][SRV][ACNT][REMV]: " +
                        "account 삭제 개수:({}), " +
                        "order_product 삭제 개수:({}), " +
                        "orders 삭제 개수:({}), " +
                        "delivery 삭제 개수:({}), " +
                        "address 삭제 개수:({}), " +
                        "cart 삭제 개수:({})"
                , account.getEmail()
                , orderProductDeletedCount
                , ordersDeletedCount
                , deliveryDeletedCount
                , addressDeletedCount
                , cartDeletedCount
        );

        return account;
    }

    public Account readByIdAndEmail(Long accountId, String email) {
        return accountRepository.findByIdAndEmail(accountId, email)
                .orElseThrow(() -> new EntityNotFoundException(Msg.ACCOUNT_NOT_FOUND));
    }

    public boolean checkNotUser(Principal principal) {
        final AccountRole accountRole = jwtService.readByPrincipal(principal).getAccountRole();
        return accountRole == AccountRole.ADMIN || accountRole == AccountRole.SELLER;
    }
}
