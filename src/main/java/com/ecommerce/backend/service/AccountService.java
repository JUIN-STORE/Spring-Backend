package com.ecommerce.backend.service;

import com.ecommerce.backend.domain.entity.Account;
import com.ecommerce.backend.domain.entity.Address;
import com.ecommerce.backend.domain.request.AccountRequest;
import com.ecommerce.backend.domain.response.AccountResponse;
import com.ecommerce.backend.repository.AccountRepository;
import com.ecommerce.backend.repository.AddressRepository;
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

/** Service Naming
 * C -> save
 * R -> findBy~
 * U -> update
 * D -> delete
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {
    private final AccountRepository accountRepository;
    private final AddressRepository addressRepository;

    private final CartService cartService;

    private void validateDuplicateAccountEmail(AccountRequest.RegisterRequest request){
        Optional<Account> validEmail = accountRepository.findByEmail(request.getEmail());
        if (validEmail.isPresent()) throw new EntityExistsException("존재하는 이메일입니다. 다른 이메일을 입력해 주세요.");
    }

    public AccountResponse.RegisterResponse saveAccount(AccountRequest.RegisterRequest request) {
        // 검사
        validateDuplicateAccountEmail(request);

        final Account account = request.toAccount();
        final Address address = request.getAddress().toAddress(account);

        accountRepository.save(account);
        addressRepository.save(address);
        cartService.createCart(account);

        return AccountResponse.RegisterResponse.fromAccount(account);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("user name not found!"));
        return new org.springframework.security.core.userdetails.User(account.getEmail(), account.getPasswordHash(), new ArrayList<>());
    }

    @Transactional(readOnly = true)
    public AccountResponse.ReadResponse findById(Long id) {
        Account account = accountRepository.findById(id).orElseThrow(EntityNotFoundException::new); // select * from cart where account_id = ? 쿼리도 나감 (오류)
        return AccountResponse.ReadResponse.fromAccount(account);
    }

    @Transactional(readOnly = true)
    public AccountResponse.ReadResponse findByEmail(String email) {
        Account account = accountRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
        return AccountResponse.ReadResponse.fromAccount(account);
    }

    public AccountResponse.UpdateResponse update(AccountRequest.UpdateRequest request, Principal principal) {
        final String email = principal.getName();
        final Account findAccount = accountRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);

        Account account = request.toAccount(findAccount.getId(), email);
        accountRepository.save(account);

        return AccountResponse.UpdateResponse.fromAccount(account);
    }

    @Transactional(rollbackFor = Exception.class)
    public AccountResponse.DeleteResponse delete(Principal principal) {
        final String email = principal.getName();
        final Account account = accountRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
        final Address address = addressRepository.findByAccountId(account.getId()).orElseThrow(EntityNotFoundException::new);

        addressRepository.delete(address);
        accountRepository.delete(account);

        return AccountResponse.DeleteResponse.fromAccount(account);
    }

    public Account findByPrincipal(Principal principal) {
        final String email = principal.getName();
        return accountRepository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
    }
}
