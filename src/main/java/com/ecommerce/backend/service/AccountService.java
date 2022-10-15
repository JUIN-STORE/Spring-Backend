package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.entity.Address;
import com.ecommerce.backend.domain.entity.Order;
import com.ecommerce.backend.domain.enums.AccountRole;
import com.ecommerce.backend.domain.request.AccountRequest;
import com.ecommerce.backend.repository.jpa.AccountRepository;
import com.ecommerce.backend.repository.jpa.OrderProductRepository;
import com.ecommerce.backend.repository.jpa.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {
    private final AccountRepository accountRepository;
    private final OrderProductRepository orderProductRepository;
    private final OrderRepository orderRepository;

    private final AddressService addressService;
    private final CartService cartService;
    private final DeliveryService deliveryService;
//    private final OrderService orderService;

    private void validateDuplicateEmail(AccountRequest.SignUp request){
        Optional<Account> validEmail = accountRepository.findByEmail(request.getEmail());
        if (validEmail.isPresent()) throw new EntityExistsException("존재하는 이메일입니다. 다른 이메일을 입력해 주세요.");
    }

    @Transactional
    public Account add(AccountRequest.SignUp request) {
        // 이메일 중복 검사
        validateDuplicateEmail(request);

        final Account account = request.toAccount();
        final Address address = request.getAddress().toAddress(account);

        accountRepository.save(account);

        addressService.addAddress(address);
        cartService.addCart(account);

        return account;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("user name not found!"));
        return new org.springframework.security.core.userdetails.User(account.getEmail(), account.getPasswordHash(), new ArrayList<>());
    }

    @Transactional(readOnly = true)
    public Account readById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new); // select * from cart where account_id = ? 쿼리도 나감 (오류)
    }

    @Transactional(readOnly = true)
    public Account readByEmail(String email) {
        return accountRepository
                .findByEmail(email)
                .orElseThrow(EntityNotFoundException::new);
    }

    @Transactional
    public Account modifyAccount(AccountRequest.Update request, Principal principal) {
        final String email = principal.getName();
        final Account findAccount = accountRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);

        Account account = request.toAccount(findAccount.getId(), email);
        accountRepository.save(account);

        return account;
    }

    @Transactional(rollbackFor = Exception.class)
    public Account removeAccount(Long accountId, Principal principal) {
        final String email = principal.getName();
        final Account account = this.readByIdAndEmail(accountId, email);

        final List<Address> addressList = addressService.readByAccountId(account.getId());
        final List<Long> addressIdList = addressList.stream().map(Address::getId).collect(Collectors.toList());

        final List<Order> orderList = orderRepository.findByAccountId(account.getId());
        final List<Long> orderIdList = orderList.stream().map(Order::getId).collect(Collectors.toList());

        orderProductRepository.deleteByOrderIdList(orderIdList);  // order_product 삭제
        orderRepository.deleteByAccountId(accountId);             // order 삭제

        deliveryService.remove(addressIdList);      // delivery 삭제
        addressService.remove(accountId);           // address 삭제
        cartService.remove(account);                // cart 삭제

        accountRepository.delete(account);          // account 삭제

        return account;
    }

    public Account readByPrincipal(Principal principal) {
        final String email = principal.getName();
        return accountRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
    }

    public boolean checkSeller(Principal principal) {
        return this.readByPrincipal(principal).getAccountRole() == AccountRole.SELLER;
    }

    public Account readByIdAndEmail(Long accountId, String email){
        return accountRepository.findByIdAndEmail(accountId, email).orElseThrow(EntityNotFoundException::new);
    }
}
