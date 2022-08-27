package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.entity.Address;
import com.ecommerce.backend.domain.request.AccountRequest;
import com.ecommerce.backend.repository.AccountRepository;
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
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {
    private final AccountRepository accountRepository;

    private final AddressService addressService;
    private final CartService cartService;

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
        return accountRepository.findById(id).orElseThrow(EntityNotFoundException::new); // select * from cart where account_id = ? 쿼리도 나감 (오류)
    }

    @Transactional(readOnly = true)
    public Account readByEmail(String email) {
        return accountRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
    }

    public Account modifyAccount(AccountRequest.Update request, Principal principal) {
        final String email = principal.getName();
        final Account findAccount = accountRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);

        Account account = request.toAccount(findAccount.getId(), email);
        accountRepository.save(account);

        return account;
    }

    @Transactional(rollbackFor = Exception.class)
    public Account removeAccount(Principal principal) {
        final String email = principal.getName();
        final Account account = accountRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
        final Address address = addressService.readById(account.getId());

        cartService.remove(account);
        addressService.remove(address);

        accountRepository.delete(account);

        return account;
    }

    public Account findByPrincipal(Principal principal) {
        final String email = principal.getName();
        return accountRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
    }
}
